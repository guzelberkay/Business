package com.usermanagement.mapper;

import com.usermanagement.dto.requestDTOs.RoleCreateDTO;
import com.usermanagement.dto.requestDTOs.UserSaveRequestDTO;
import com.usermanagement.dto.responseDTOs.RoleResponseDTO;
import com.usermanagement.entity.Role;
import com.usermanagement.entity.User;
import com.usermanagement.views.GetAllRoleView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);


    Role roleCreateDTOToRole(RoleCreateDTO roleCreateDTO);

    RoleResponseDTO getAllRoleViewToRoleResponseDTO(GetAllRoleView getAllRoleView);

}