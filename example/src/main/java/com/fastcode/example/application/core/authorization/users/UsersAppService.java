package com.fastcode.example.application.core.authorization.users;

import com.fastcode.example.addons.reporting.domain.dashboarduser.*;
import com.fastcode.example.addons.reporting.domain.dashboardversion.*;
import com.fastcode.example.addons.reporting.domain.dashboardversionreport.*;
import com.fastcode.example.addons.reporting.domain.reportuser.*;
import com.fastcode.example.addons.reporting.domain.reportversion.*;
import com.fastcode.example.application.core.authorization.users.dto.*;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.*;
import com.fastcode.example.domain.core.authorization.users.IUsersRepository;
import com.fastcode.example.domain.core.authorization.users.QUsers;
import com.fastcode.example.domain.core.authorization.users.Users;
import com.fastcode.example.domain.core.authorization.userspreference.IUserspreferenceRepository;
import com.fastcode.example.domain.core.authorization.userspreference.Userspreference;
import com.fastcode.example.security.SecurityUtils;
import com.querydsl.core.BooleanBuilder;
import java.time.*;
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

@Service("usersAppService")
@RequiredArgsConstructor
public class UsersAppService implements IUsersAppService {

    @NonNull
    protected final IDashboarduserRepository _dashboarduserRepository;

    @NonNull
    protected final IReportuserRepository _reportuserRepository;

    @Qualifier("dashboardversionRepository")
    @NonNull
    protected final IDashboardversionRepository _dashboardversionRepository;

    @Qualifier("reportversionRepository")
    @NonNull
    protected final IReportversionRepository _reportversionRepository;

    @Qualifier("dashboardversionreportRepository")
    @NonNull
    protected final IDashboardversionreportRepository _reportDashboardRepository;

    public static final long PASSWORD_TOKEN_EXPIRATION_TIME = 3_600_000; // 1 hour

    @Qualifier("usersRepository")
    @NonNull
    protected final IUsersRepository _usersRepository;

    @Qualifier("userspreferenceRepository")
    @NonNull
    protected final IUserspreferenceRepository _userspreferenceRepository;

    @Qualifier("IUsersMapperImpl")
    @NonNull
    protected final IUsersMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateUsersOutput create(CreateUsersInput input) {
        Users users = mapper.createUsersInputToUsers(input);

        Users createdUsers = _usersRepository.save(users);
        Userspreference usersPreference = createDefaultUsersPreference(createdUsers);
        return mapper.usersToCreateUsersOutput(createdUsers, usersPreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateUsersOutput update(Long usersId, UpdateUsersInput input) {
        Users existing = _usersRepository.findById(usersId).get();

        Users users = mapper.updateUsersInputToUsers(input);
        users.setDashboardsSet(existing.getDashboardsSet());
        users.setDashboardversionsSet(existing.getDashboardversionsSet());
        users.setReportsSet(existing.getReportsSet());
        users.setReportversionsSet(existing.getReportversionsSet());
        users.setTimesheetsSet(existing.getTimesheetsSet());
        users.setTokenverificationsSet(existing.getTokenverificationsSet());
        users.setUserspermissionsSet(existing.getUserspermissionsSet());
        users.setUsersrolesSet(existing.getUsersrolesSet());
        users.setUsertasksSet(existing.getUsertasksSet());

        Users updatedUsers = _usersRepository.save(users);
        return mapper.usersToUpdateUsersOutput(updatedUsers);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long usersId) {
        Users existing = _usersRepository.findById(usersId).orElse(null);

        List<Dashboardversionreport> dvrList = _reportDashboardRepository.findByUsersId(usersId);
        for (Dashboardversionreport dvr : dvrList) {
            _reportDashboardRepository.delete(dvr);
        }

        List<Dashboarduser> duList = _dashboarduserRepository.findByUsersId(usersId);
        for (Dashboarduser du : duList) {
            _dashboarduserRepository.delete(du);
        }

        List<Reportuser> ruList = _reportuserRepository.findByUsersId(usersId);
        for (Reportuser du : ruList) {
            _reportuserRepository.delete(du);
        }

        List<Dashboardversion> dvList = _dashboardversionRepository.findByUsersId(usersId);
        for (Dashboardversion du : dvList) {
            _dashboardversionRepository.delete(du);
        }

        List<Reportversion> rvList = _reportversionRepository.findByUsersId(usersId);
        for (Reportversion rv : rvList) {
            _reportversionRepository.delete(rv);
        }

        Userspreference userspreference = _userspreferenceRepository.findById(usersId).orElse(null);
        _userspreferenceRepository.delete(userspreference);

        _usersRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersByIdOutput findById(Long usersId) {
        Users foundUsers = _usersRepository.findById(usersId).orElse(null);
        if (foundUsers == null) return null;

        Userspreference usersPreference = _userspreferenceRepository.findById(usersId).orElse(null);

        return mapper.usersToFindUsersByIdOutput(foundUsers, usersPreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Userspreference createDefaultUsersPreference(Users users) {
        Userspreference userspreference = new Userspreference();
        userspreference.setTheme("default-theme");
        userspreference.setLanguage("en");
        userspreference.setId(users.getId());
        userspreference.setUsers(users);

        return _userspreferenceRepository.save(userspreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTheme(Users users, String theme) {
        Userspreference userspreference = _userspreferenceRepository.findById(users.getId()).orElse(null);
        userspreference.setTheme(theme);

        _userspreferenceRepository.save(userspreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLanguage(Users users, String language) {
        Userspreference userspreference = _userspreferenceRepository.findById(users.getId()).orElse(null);
        userspreference.setLanguage(language);

        _userspreferenceRepository.save(userspreference);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersWithAllFieldsByIdOutput findWithAllFieldsById(Long usersId) {
        Users foundUsers = _usersRepository.findById(usersId).orElse(null);
        if (foundUsers == null) return null;

        return mapper.usersToFindUsersWithAllFieldsByIdOutput(foundUsers);
    }

    public UsersProfile getProfile(FindUsersByIdOutput user) {
        return mapper.findUsersByIdOutputToUsersProfile(user);
    }

    public UsersProfile updateUsersProfile(FindUsersWithAllFieldsByIdOutput users, UsersProfile usersProfile) {
        UpdateUsersInput usersInput = mapper.findUsersWithAllFieldsByIdOutputAndUsersProfileToUpdateUsersInput(
            users,
            usersProfile
        );
        UpdateUsersOutput output = update(users.getId(), usersInput);

        return mapper.updateUsersOutputToUsersProfile(output);
    }

    @Transactional(readOnly = true)
    public Users getUsers() {
        return _usersRepository.findByUsernameIgnoreCase(SecurityUtils.getCurrentUserLogin().orElse(null));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersByUsernameOutput findByUsername(String username) {
        Users foundUsers = _usersRepository.findByUsernameIgnoreCase(username);
        if (foundUsers == null) {
            return null;
        }

        return mapper.usersToFindUsersByUsernameOutput(foundUsers);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersByUsernameOutput findByEmailaddress(String emailAddress) {
        Users foundUsers = _usersRepository.findByEmailaddressIgnoreCase(emailAddress);
        if (foundUsers == null) {
            return null;
        }

        return mapper.usersToFindUsersByUsernameOutput(foundUsers);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUsersData(FindUsersWithAllFieldsByIdOutput users) {
        Users foundUsers = mapper.findUsersWithAllFieldsByIdOutputToUsers(users);
        _usersRepository.save(foundUsers);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindUsersByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<Users> foundUsers = _usersRepository.findAll(search(search), pageable);
        List<Users> usersList = foundUsers.getContent();
        Iterator<Users> usersIterator = usersList.iterator();
        List<FindUsersByIdOutput> output = new ArrayList<>();

        while (usersIterator.hasNext()) {
            Users users = usersIterator.next();
            Userspreference usersPreference = _userspreferenceRepository.findById(users.getId()).orElse(null);
            output.add(mapper.usersToFindUsersByIdOutput(users, usersPreference));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QUsers users = QUsers.usersEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(users, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("emailaddress") ||
                    list.get(i).replace("%20", "").trim().equals("firstname") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("isactive") ||
                    list.get(i).replace("%20", "").trim().equals("isemailconfirmed") ||
                    list.get(i).replace("%20", "").trim().equals("joinDate") ||
                    list.get(i).replace("%20", "").trim().equals("lastname") ||
                    list.get(i).replace("%20", "").trim().equals("password") ||
                    list.get(i).replace("%20", "").trim().equals("username")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QUsers users,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("emailaddress")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.emailaddress.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.emailaddress.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.emailaddress.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("firstname")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.firstname.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.firstname.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.firstname.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("id")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(users.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(users.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            users.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(users.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(users.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("isactive")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(users.isactive.eq(Boolean.parseBoolean(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(users.isactive.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("isemailconfirmed")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(users.isemailconfirmed.eq(Boolean.parseBoolean(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(users.isemailconfirmed.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("joinDate")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    SearchUtils.stringToLocalDate(details.getValue().getSearchValue()) != null
                ) {
                    builder.and(users.joinDate.eq(SearchUtils.stringToLocalDate(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    SearchUtils.stringToLocalDate(details.getValue().getSearchValue()) != null
                ) {
                    builder.and(users.joinDate.ne(SearchUtils.stringToLocalDate(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    LocalDate startLocalDate = SearchUtils.stringToLocalDate(details.getValue().getStartingValue());
                    LocalDate endLocalDate = SearchUtils.stringToLocalDate(details.getValue().getEndingValue());
                    if (startLocalDate != null && endLocalDate != null) {
                        builder.and(users.joinDate.between(startLocalDate, endLocalDate));
                    } else if (endLocalDate != null) {
                        builder.and(users.joinDate.loe(endLocalDate));
                    } else if (startLocalDate != null) {
                        builder.and(users.joinDate.goe(startLocalDate));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("lastname")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.lastname.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.lastname.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.lastname.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("password")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.password.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.password.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.password.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("username")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.username.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.username.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.username.ne(details.getValue().getSearchValue()));
                }
            }
        }

        return builder;
    }

    public Map<String, String> parseDashboardsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseDashboardversionsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseReportsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseReportversionsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseTimesheetsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("userid", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseUserspermissionsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseUsersrolesJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseUsertasksJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("userid", keysString);

        return joinColumnMap;
    }
}
