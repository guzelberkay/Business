package com.usermanagement.views;

import com.usermanagement.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetAllUsersView {
    private String firstName;
    private String lastName;
    private List<Role> userRoles;

}
