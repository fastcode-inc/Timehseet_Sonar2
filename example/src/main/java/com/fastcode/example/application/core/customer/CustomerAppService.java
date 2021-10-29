package com.fastcode.example.application.core.customer;

import com.fastcode.example.application.core.customer.dto.*;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.*;
import com.fastcode.example.domain.core.customer.Customer;
import com.fastcode.example.domain.core.customer.ICustomerRepository;
import com.fastcode.example.domain.core.customer.QCustomer;
import com.querydsl.core.BooleanBuilder;

import java.net.MalformedURLException;
import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service("customerAppService")
@RequiredArgsConstructor
public class CustomerAppService implements ICustomerAppService {

    public static final String CONTAINS = "contains";
    public static final String EQUALS_TO = "equals";
    public static final String NOT_EQUAL = "notEqual";
    @Qualifier("customerRepository")
    @NonNull
    protected final ICustomerRepository _customerRepository;

    @Qualifier("ICustomerMapperImpl")
    @NonNull
    protected final ICustomerMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateCustomerOutput create(CreateCustomerInput input) {
        Customer customer = mapper.createCustomerInputToCustomer(input);

        Customer createdCustomer = _customerRepository.save(customer);
        return mapper.customerToCreateCustomerOutput(createdCustomer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateCustomerOutput update(Long customerId, UpdateCustomerInput input) {
        Customer existing = null;
        Optional<Customer> c = _customerRepository.findById(customerId);
        if(c.isPresent())
            existing = c.get();
        else {
            throw new EntityNotFoundException("Entity not found");
        }

        Customer customer = mapper.updateCustomerInputToCustomer(input);
        customer.setProjectsSet(existing.getProjectsSet());

        Customer updatedCustomer = _customerRepository.save(customer);
        return mapper.customerToUpdateCustomerOutput(updatedCustomer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long customerId) {
        Customer existing = _customerRepository.findById(customerId).orElse(null);
        if(existing !=null) {
            _customerRepository.delete(existing);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindCustomerByIdOutput findById(Long customerId) {
        Customer foundCustomer = _customerRepository.findById(customerId).orElse(null);
        if (foundCustomer == null) return null;

        return mapper.customerToFindCustomerByIdOutput(foundCustomer);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindCustomerByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Customer> foundCustomer = _customerRepository.findAll(search(search), pageable);
        List<Customer> customerList = foundCustomer.getContent();
        Iterator<Customer> customerIterator = customerList.iterator();
        List<FindCustomerByIdOutput> output = new ArrayList<>();

        while (customerIterator.hasNext()) {
            Customer customer = customerIterator.next();
            output.add(mapper.customerToFindCustomerByIdOutput(customer));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QCustomer customer = QCustomer.customerEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(customer, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("customerid") ||
                    list.get(i).replace("%20", "").trim().equals("description") ||
                    list.get(i).replace("%20", "").trim().equals("isactive") ||
                    list.get(i).replace("%20", "").trim().equals("name")
                )
            ) {
                throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QCustomer customer,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        Iterator<Map.Entry<String, SearchFields>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SearchFields> details = iterator.next();
            if (details.getKey().replace("%20", "").trim().equals("customerid")) {
                if (details.getValue().getOperator().equals(CONTAINS)) {
                    builder.and(customer.customerid.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(customer.customerid.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(customer.customerid.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            customer.customerid.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(customer.customerid.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(customer.customerid.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("description")) {
                if (details.getValue().getOperator().equals(CONTAINS)) {
                    builder.and(customer.description.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals(EQUALS_TO)) {
                    builder.and(customer.description.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals(NOT_EQUAL)) {
                    builder.and(customer.description.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("isactive")) {
                if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(customer.isactive.eq(Boolean.parseBoolean(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(customer.isactive.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("name")) {
                if (details.getValue().getOperator().equals(CONTAINS)) {
                    builder.and(customer.name.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals(EQUALS_TO)) {
                    builder.and(customer.name.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals(NOT_EQUAL)) {
                    builder.and(customer.name.ne(details.getValue().getSearchValue()));
                }
            }
        }

        return builder;
    }

    public Map<String, String> parseProjectsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("customerid", keysString);

        return joinColumnMap;
    }
}
