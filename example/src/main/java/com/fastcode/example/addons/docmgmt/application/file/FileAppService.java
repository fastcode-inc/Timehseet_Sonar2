package com.fastcode.example.addons.docmgmt.application.file;

import com.fastcode.example.addons.docmgmt.application.file.dto.*;
import com.fastcode.example.addons.docmgmt.domain.file.FileEntity;
import com.fastcode.example.addons.docmgmt.domain.file.IFileRepository;
import com.fastcode.example.addons.docmgmt.domain.file.QFileEntity;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchFields;
import com.fastcode.example.commons.search.SearchUtils;
import com.querydsl.core.BooleanBuilder;

import java.net.MalformedURLException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service("fileAppService")
public class FileAppService implements IFileAppService {

    public static final String EQUALS_TO = "equals";
    public static final String NOT_EQUAL = "notEqual";
    public static final String RANGE = "range";
    public static final String CONTAINS = "contains";
    @Autowired
    @Qualifier("fileRepository")
    protected IFileRepository _fileRepository;

    @Autowired
    @Qualifier("IFileMapperImpl")
    protected IFileMapper mapper;

    @Autowired
    protected LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateFileOutput create(CreateFileInput input) {
        FileEntity file = mapper.createFileInputToFileEntity(input);

        if (file.getCreated() == null) {
            file.setCreated(new Date());
        }

        FileEntity createdFile = _fileRepository.save(file);
        return mapper.fileEntityToCreateFileOutput(createdFile);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateFileOutput update(Long fileId, UpdateFileInput input) {
        FileEntity existing = null;
        Optional<FileEntity> f = _fileRepository.findById(fileId);
        if(f.isPresent())
            existing = f.get();
        else
            throw new EntityNotFoundException("Enity not found");

        FileEntity file = mapper.updateFileInputToFileEntity(input);

        file.setVersiono(existing.getVersiono());
        file.setVersionp(existing.getVersionp());

        FileEntity updatedFile = _fileRepository.save(file);
        return mapper.fileEntityToUpdateFileOutput(updatedFile);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long fileId) {
        FileEntity existing = _fileRepository.findById(fileId).orElse(null);
        _fileRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindFileByIdOutput findById(Long fileId) {
        FileEntity foundFile = _fileRepository.findById(fileId).orElse(null);
        if (foundFile == null) return null;

        return mapper.fileEntityToFindFileByIdOutput(foundFile);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindFileByIdOutput> find(SearchCriteria search, Pageable pageable) throws MalformedURLException{
        Page<FileEntity> foundFile = _fileRepository.findAll(search(search), pageable);
        List<FileEntity> fileList = foundFile.getContent();
        Iterator<FileEntity> fileIterator = fileList.iterator();
        List<FindFileByIdOutput> output = new ArrayList<>();

        while (fileIterator.hasNext()) {
            FileEntity file = fileIterator.next();
            output.add(mapper.fileEntityToFindFileByIdOutput(file));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws MalformedURLException{
        QFileEntity file = QFileEntity.fileEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(file, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws MalformedURLException {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("ancestorId") ||
                    list.get(i).replace("%20", "").trim().equals("ancestralRootId") ||
                    list.get(i).replace("%20", "").trim().equals("contentId") ||
                    list.get(i).replace("%20", "").trim().equals("contentLength") ||
                    list.get(i).replace("%20", "").trim().equals("created") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("label") ||
                    list.get(i).replace("%20", "").trim().equals("lockOwner") ||
                    list.get(i).replace("%20", "").trim().equals("mimeType") ||
                    list.get(i).replace("%20", "").trim().equals("name") ||
                    list.get(i).replace("%20", "").trim().equals("successorId") ||
                    list.get(i).replace("%20", "").trim().equals("summary") ||
                    list.get(i).replace("%20", "").trim().equals("versiono") ||
                    list.get(i).replace("%20", "").trim().equals("versionp")
                )
            ) {
                throw new MalformedURLException("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QFileEntity file,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("ancestorId")) {
                if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.ancestorId.eq(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.ancestorId.ne(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(RANGE)
                ) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) builder.and(
                        file.ancestorId.between(
                            Long.valueOf(details.getValue().getStartingValue()),
                            Long.valueOf(details.getValue().getEndingValue())
                        )
                    ); else if (StringUtils.isNumeric(details.getValue().getStartingValue())) builder.and(
                        file.ancestorId.goe(Long.valueOf(details.getValue().getStartingValue()))
                    ); else if (StringUtils.isNumeric(details.getValue().getEndingValue())) builder.and(
                        file.ancestorId.loe(Long.valueOf(details.getValue().getEndingValue()))
                    );
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("ancestralRootId")) {
                if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.ancestralRootId.eq(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.ancestralRootId.ne(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(RANGE)
                ) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) builder.and(
                        file.ancestralRootId.between(
                            Long.valueOf(details.getValue().getStartingValue()),
                            Long.valueOf(details.getValue().getEndingValue())
                        )
                    ); else if (StringUtils.isNumeric(details.getValue().getStartingValue())) builder.and(
                        file.ancestralRootId.goe(Long.valueOf(details.getValue().getStartingValue()))
                    ); else if (StringUtils.isNumeric(details.getValue().getEndingValue())) builder.and(
                        file.ancestralRootId.loe(Long.valueOf(details.getValue().getEndingValue()))
                    );
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("contentId")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    file.contentId.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    file.contentId.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    file.contentId.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("contentLength")) {
                if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.contentLength.eq(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.contentLength.ne(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(RANGE)
                ) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) builder.and(
                        file.contentLength.between(
                            Long.valueOf(details.getValue().getStartingValue()),
                            Long.valueOf(details.getValue().getEndingValue())
                        )
                    ); else if (StringUtils.isNumeric(details.getValue().getStartingValue())) builder.and(
                        file.contentLength.goe(Long.valueOf(details.getValue().getStartingValue()))
                    ); else if (StringUtils.isNumeric(details.getValue().getEndingValue())) builder.and(
                        file.contentLength.loe(Long.valueOf(details.getValue().getEndingValue()))
                    );
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("created")) {
                if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    SearchUtils.stringToDate(details.getValue().getSearchValue()) != null
                ) builder.and(file.created.eq(SearchUtils.stringToDate(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    SearchUtils.stringToDate(details.getValue().getSearchValue()) != null
                ) builder.and(file.created.ne(SearchUtils.stringToDate(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(RANGE)
                ) {
                    Date startDate = SearchUtils.stringToDate(details.getValue().getStartingValue());
                    Date endDate = SearchUtils.stringToDate(details.getValue().getEndingValue());
                    if (startDate != null && endDate != null) builder.and(
                        file.created.between(startDate, endDate)
                    ); else if (endDate != null) builder.and(file.created.loe(endDate)); else if (
                        startDate != null
                    ) builder.and(file.created.goe(startDate));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("label")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    file.label.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    file.label.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    file.label.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("lockOwner")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    file.lockOwner.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    file.lockOwner.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    file.lockOwner.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("mimeType")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    file.mimeType.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    file.mimeType.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    file.mimeType.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("name")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    file.name.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    file.name.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    file.name.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("successorId")) {
                if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.successorId.eq(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.successorId.ne(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(RANGE)
                ) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) builder.and(
                        file.successorId.between(
                            Long.valueOf(details.getValue().getStartingValue()),
                            Long.valueOf(details.getValue().getEndingValue())
                        )
                    ); else if (StringUtils.isNumeric(details.getValue().getStartingValue())) builder.and(
                        file.successorId.goe(Long.valueOf(details.getValue().getStartingValue()))
                    ); else if (StringUtils.isNumeric(details.getValue().getEndingValue())) builder.and(
                        file.successorId.loe(Long.valueOf(details.getValue().getEndingValue()))
                    );
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("summary")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    file.summary.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    file.summary.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    file.summary.ne(details.getValue().getSearchValue())
                );
            }
            if (details.getKey().replace("%20", "").trim().equals("versiono")) {
                if (
                    details.getValue().getOperator().equals(EQUALS_TO) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.versiono.eq(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(NOT_EQUAL) &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) builder.and(file.versiono.ne(Long.valueOf(details.getValue().getSearchValue()))); else if (
                    details.getValue().getOperator().equals(RANGE)
                ) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) builder.and(
                        file.versiono.between(
                            Long.valueOf(details.getValue().getStartingValue()),
                            Long.valueOf(details.getValue().getEndingValue())
                        )
                    ); else if (StringUtils.isNumeric(details.getValue().getStartingValue())) builder.and(
                        file.versiono.goe(Long.valueOf(details.getValue().getStartingValue()))
                    ); else if (StringUtils.isNumeric(details.getValue().getEndingValue())) builder.and(
                        file.versiono.loe(Long.valueOf(details.getValue().getEndingValue()))
                    );
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("versionp")) {
                if (details.getValue().getOperator().equals(CONTAINS)) builder.and(
                    file.versionp.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                ); else if (details.getValue().getOperator().equals(EQUALS_TO)) builder.and(
                    file.versionp.eq(details.getValue().getSearchValue())
                ); else if (details.getValue().getOperator().equals(NOT_EQUAL)) builder.and(
                    file.versionp.ne(details.getValue().getSearchValue())
                );
            }
        }

        return builder;
    }
}
