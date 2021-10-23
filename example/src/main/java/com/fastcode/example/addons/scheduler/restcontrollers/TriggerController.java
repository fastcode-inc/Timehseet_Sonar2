package com.fastcode.example.addons.scheduler.restcontrollers;

import com.fastcode.example.addons.scheduler.application.trigger.ITriggerAppService;
import com.fastcode.example.addons.scheduler.application.trigger.dto.*;
import com.fastcode.example.commons.search.OffsetBasedPageRequest;
import com.fastcode.example.commons.search.SearchCriteria;
import com.fastcode.example.commons.search.SearchUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/scheduler/triggers")
public class TriggerController {

    @Autowired
    @Qualifier("triggerAppService")
    protected ITriggerAppService triggerAppService;

    @Autowired
    protected Environment env;

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_CREATE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<CreateTriggerInput> createTrigger(@RequestBody @Valid CreateTriggerInput obj)
        throws Exception {
        if (
            obj.getJobName() == null ||
            obj.getJobGroup() == null ||
            obj.getTriggerName() == null ||
            obj.getTriggerGroup() == null
        ) {
            throw new IOException(String.format("Invalid input"));
        }
        boolean status = triggerAppService.createTrigger(obj);
        if (!status) {
            throw new IOException(String.format("Invalid input"));
        }

        return new ResponseEntity(obj, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_UPDATE')")
    @RequestMapping(
        value = "/{triggerName}/{triggerGroup}",
        method = RequestMethod.PUT,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<String> updateTrigger(
        @PathVariable String triggerName,
        @PathVariable String triggerGroup,
        @RequestBody @Valid UpdateTriggerInput obj
    )
        throws SchedulerException, IOException {
        if (triggerName == null || triggerGroup == null) {
            throw new IOException(String.format("Invalid input"));
        }

        obj.setTriggerName(triggerName);
        obj.setTriggerGroup(triggerGroup);
        boolean status = triggerAppService.updateTrigger(obj);
        if (!status) {
            throw new EntityNotFoundException(String.format("There does not exist a trigger "));
        }

        Optional
            .ofNullable(status)
            .orElseThrow(() -> new EntityNotFoundException(String.format("There does not exist a trigger ")));
        return new ResponseEntity("Trigger Updation Status " + status, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_READ')")
    @RequestMapping(
        value = "/{triggerName}/{triggerGroup}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<GetTriggerOutput> returnTrigger(
        @PathVariable String triggerName,
        @PathVariable String triggerGroup
    )
        throws SchedulerException, IOException {
        if (triggerName == null || triggerGroup == null) {
            throw new IOException(String.format("Invalid input"));
        }

        GetTriggerOutput output = triggerAppService.returnTrigger(triggerName, triggerGroup);

        Optional
            .ofNullable(output)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(
                            "There does not exist a trigger with a triggerName=%s and triggerGroup=%s",
                            triggerName,
                            triggerGroup
                        )
                    )
            );
        return new ResponseEntity(output, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_READ')")
    @RequestMapping(method = RequestMethod.GET, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<List<GetTriggerOutput>> listAllTriggers(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "offset", required = false) String offset,
        @RequestParam(value = "limit", required = false) String limit,
        Sort sort
    )
        throws Exception {
        if (offset == null) {
            offset = env.getProperty("fastCode.offset.default");
        }
        if (limit == null) {
            limit = env.getProperty("fastCode.limit.default");
        }
        //if (sort.isUnsorted()) { sort = new Sort(Sort.Direction.fromString(env.getProperty("fastCode.sort.direction.default")), new String[]{env.getProperty("fastCode.sort.property.default")}); }

        Pageable offsetPageable = new OffsetBasedPageRequest(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        SearchCriteria searchCriteria = SearchUtils.generateSearchCriteriaObject(search);
        List<GetTriggerOutput> list = triggerAppService.listAllTriggers(searchCriteria, offsetPageable);

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_READ')")
    @RequestMapping(
        value = "/getTriggerGroups",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<String>> listAllTriggerGroups() throws SchedulerException {
        List<String> list = triggerAppService.listAllTriggerGroups();

        return new ResponseEntity(list, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_READ')")
    @RequestMapping(
        value = "/pauseTrigger/{triggerName}/{triggerGroup}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<Boolean> pauseTrigger(@PathVariable String triggerName, @PathVariable String triggerGroup)
        throws SchedulerException, IOException {
        if (triggerName == null || triggerGroup == null) {
            throw new IOException(String.format("Invalid input"));
        }
        boolean status = triggerAppService.pauseTrigger(triggerName, triggerGroup);
        Optional
            .ofNullable(status)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(
                            "There does not exist a trigger with a triggerName=%s and triggerGroup=%s",
                            triggerName,
                            triggerGroup
                        )
                    )
            );

        return new ResponseEntity(status, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_READ')")
    @RequestMapping(
        value = "/resumeTrigger/{triggerName}/{triggerGroup}",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<Boolean> resumeTrigger(@PathVariable String triggerName, @PathVariable String triggerGroup)
        throws SchedulerException, IOException {
        if (triggerName == null || triggerGroup == null) {
            throw new IOException(String.format("Invalid input"));
        }
        boolean status = triggerAppService.resumeTrigger(triggerName, triggerGroup);
        Optional
            .ofNullable(status)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(
                            "There does not exist a trigger with a triggerName=%s and triggerGroup=%s",
                            triggerName,
                            triggerGroup
                        )
                    )
            );

        return new ResponseEntity(status, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_DELETE')")
    @RequestMapping(
        value = "/{triggerName}/{triggerGroup}",
        method = RequestMethod.DELETE,
        consumes = { "application/json" }
    )
    public ResponseEntity<Boolean> cancelTrigger(@PathVariable String triggerName, @PathVariable String triggerGroup)
        throws SchedulerException, IOException {
        if (triggerName == null || triggerGroup == null) {
            throw new IOException(String.format("Invalid input"));
        }
        boolean status = triggerAppService.cancelTrigger(triggerName, triggerGroup);
        Optional
            .ofNullable(status)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(
                            "There does not exist a trigger with a triggerName=%s and triggerGroup=%s",
                            triggerName,
                            triggerGroup
                        )
                    )
            );

        return new ResponseEntity(status, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRIGGERDETAILSENTITY_READ')")
    @RequestMapping(
        value = "/{triggerName}/{triggerGroup}/jobExecutionHistory",
        method = RequestMethod.GET,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<List<GetJobOutput>> executionHistoryByTrigger(
        @PathVariable String triggerName,
        @PathVariable String triggerGroup
    )
        throws IOException {
        if (triggerName == null || triggerGroup == null) {
            throw new IOException(String.format("Invalid input"));
        }
        return new ResponseEntity(
            triggerAppService.executionHistoryByTrigger(triggerName, triggerGroup),
            HttpStatus.OK
        );
    }
}
