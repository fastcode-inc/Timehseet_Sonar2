package com.fastcode.example.addons.emailbuilder.restcontrollers;

import com.fastcode.example.addons.docmgmt.domain.file.FileEntity;
import com.fastcode.example.addons.docmgmt.domain.file.IFileContentStore;
import com.fastcode.example.addons.docmgmt.domain.file.IFileRepository;
import com.fastcode.example.addons.docmgmt.domain.filehistory.IFileHistoryRepository;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileContentController {

    @Autowired
    private IFileRepository filesRepo;

    @Autowired
    private IFileContentStore contentStore;

    @Autowired
    private IFileHistoryRepository fileHistoryRepo;

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.PUT)
    public ResponseEntity<Object> setContent(@PathVariable("fileId") Long id, @RequestParam("file") MultipartFile file)
        throws IOException {
        Optional<FileEntity> f = filesRepo.findById(id);
        if (f.isPresent()) {
            f.get().setMimeType(file.getContentType());
            contentStore.setContent(f.get(), file.getInputStream());
            // save updated content-related info
            filesRepo.save(f.get());
            return new ResponseEntity<Object>(HttpStatus.OK);
        }
        return null;
    }

    @RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getContent(@PathVariable("fileId") Long id) {
        Optional<FileEntity> f = filesRepo.findById(id);
        if (f.isPresent()) {
            InputStreamResource inputStreamResource = new InputStreamResource(contentStore.getContent(f.get()));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(f.get().getContentLength());
            headers.set("Content-Type", f.get().getMimeType());
            return new ResponseEntity<Object>(inputStreamResource, headers, HttpStatus.OK);
        }
        return null;
    }
}
