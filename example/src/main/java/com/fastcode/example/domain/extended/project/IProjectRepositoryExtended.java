package com.fastcode.example.domain.extended.project;

import com.fastcode.example.domain.core.project.IProjectRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("projectRepositoryExtended")
public interface IProjectRepositoryExtended extends IProjectRepository {
    //Add your custom code here
}
