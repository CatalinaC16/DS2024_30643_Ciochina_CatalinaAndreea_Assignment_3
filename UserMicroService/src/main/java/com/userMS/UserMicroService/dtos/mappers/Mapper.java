package com.userMS.UserMicroService.dtos.mappers;

public interface Mapper<Source, Target> {

    Target convertToDTO(Source source);

    Source convertToEntity(Target target);
}