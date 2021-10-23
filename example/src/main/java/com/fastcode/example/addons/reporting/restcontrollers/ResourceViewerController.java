package com.fastcode.example.addons.reporting.restcontrollers;

import com.fastcode.example.addons.reporting.application.permalink.IPermalinkAppService;
import com.fastcode.example.addons.reporting.application.permalink.dto.*;
import com.fastcode.example.addons.reporting.application.resourceviewer.IResourceViewerAppService;
import com.fastcode.example.addons.reporting.application.resourceviewer.dto.ResourceOutput;
import com.fastcode.example.commons.domain.EmptyJsonResponse;
import com.fastcode.example.commons.logging.LoggingHelper;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporting/viewResource")
public class ResourceViewerController {

    @Autowired
    private IResourceViewerAppService _resourceViewerAppService;

    @Autowired
    private IPermalinkAppService _permalinkAppService;

    public ResourceViewerController(
        IPermalinkAppService permalinkAppService,
        IResourceViewerAppService resourceViewerAppService,
        LoggingHelper helper
    ) {
        super();
        this._permalinkAppService = permalinkAppService;
        this._resourceViewerAppService = resourceViewerAppService;
    }

    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<ResourceOutput> findById(
        @PathVariable String id,
        @RequestParam(value = "password", required = false) String password
    ) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);
        }

        FindPermalinkByIdOutput permalink = _permalinkAppService.findById(uuid);
        Optional.ofNullable(permalink).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        if (!_resourceViewerAppService.isAuthorized(permalink, password)) {
            return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.NOT_FOUND);
        }

        ResourceOutput output = _resourceViewerAppService.getData(permalink);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("Not found")));

        return new ResponseEntity(output, HttpStatus.OK);
    }
}
