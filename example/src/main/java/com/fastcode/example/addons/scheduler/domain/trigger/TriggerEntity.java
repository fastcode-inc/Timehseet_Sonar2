package com.fastcode.example.addons.scheduler.domain.trigger;

import java.io.Serializable;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "qrtz_triggers")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class TriggerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //@Column(name = "id", nullable = true)
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Long id;

    @Basic
    @Column(name = "sched_name", nullable = false, length = 256)
    private String schedName;

    @Basic
    @Column(name = "job_name", nullable = false, length = 256)
    private String jobName;

    @Basic
    @Column(name = "job_group", nullable = false, length = 256)
    private String jobGroup;

    @Basic
    @Column(name = "description", nullable = true, length = 256)
    private String description;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "trigger_name", nullable = false, length = 256)
    private String triggerName;

    @Basic
    @Column(name = "trigger_group", nullable = false, length = 256)
    private String triggerGroup;

    @Basic
    @Column(name = "next_fire_time", nullable = true, length = 256)
    private Long nextFireTime;

    @Basic
    @Column(name = "prev_fire_time", nullable = true, length = 256)
    private Long prevFireTime;

    @Basic
    @Column(name = "priority", nullable = true, length = 256)
    private String priority;

    @Basic
    @Column(name = "trigger_state", nullable = false, length = 256)
    private String triggerState;

    @Basic
    @Column(name = "trigger_type", nullable = false, length = 256)
    private String triggerType;

    @Basic
    @Column(name = "start_time", nullable = false, length = 256)
    private Long startTime;

    @Basic
    @Column(name = "end_time", nullable = true, length = 256)
    private Long endTime;

    @Basic
    @Column(name = "calendar_name", nullable = true, length = 256)
    private String calendarName;

    @Basic
    @Column(name = "misfire_instr", nullable = true, length = 256)
    private String misfireInstr;

    @Basic
    @Column(name = "job_data", nullable = true, length = 256)
    private byte[] jobData;
}
