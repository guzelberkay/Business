package com.businessapi.service;

import com.businessapi.RabbitMQ.Model.CustomerSaveMailModel;
import com.businessapi.dto.request.*;
import com.businessapi.dto.response.CustomerResponseForOpportunityDTO;
import com.businessapi.dto.response.OpportunityResponseDTO;
import com.businessapi.entity.Customer;
import com.businessapi.exception.CustomerServiceException;
import com.businessapi.exception.ErrorType;
import com.businessapi.repository.CustomerRepository;
import com.businessapi.utility.SessionManager;
import com.businessapi.utility.enums.EStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;


    public Boolean save(CustomerSaveDTO dto) {
        isSavedCustomer(dto);
        Customer customer = Customer.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phone(dto.phone())
                .address(dto.address())
                .memberId(SessionManager.getMemberIdFromAuthenticatedMember())
                .build();
        customer.setStatus(EStatus.ACTIVE);
        customerRepository.save(customer);
        return true;
    }
    public boolean sendEmailExternalSourceCustomers(String email){
        CustomerSaveMailModel model = CustomerSaveMailModel.builder()
                .email(email)
                .memberId(SessionManager.getMemberIdFromAuthenticatedMember())
                .build();
        rabbitTemplate.convertAndSend("businessDirectExchange","keySaveCustomerSendMail", model);
        return true;
    }
    public Boolean saveExternalSourceCustomers(CustomerSaveLinkDTO dto){
        if (customerRepository.findCustomerByEmailIgnoreCase(dto.email()).isPresent() && customerRepository.existsCustomerByPhone(dto.phone())) {
            throw new CustomerServiceException(ErrorType.CUSTOMER_ALREADY_EXIST);
        }

        Customer customer = Customer.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phone(dto.phone())
                .address(dto.address())
                .memberId(dto.memberId())
                .build();
        customer.setStatus(EStatus.ACTIVE);
        customerRepository.save(customer);
        return true;

    }

    private void isSavedCustomer(CustomerSaveDTO dto) {
        if (customerRepository.findCustomerByEmailIgnoreCase(dto.email()).isPresent() && customerRepository.existsCustomerByPhone(dto.phone())) {
            throw new CustomerServiceException(ErrorType.CUSTOMER_ALREADY_EXIST);
        }
    }

    public void saveForDemoData(CustomerSaveDemoDTO dto) {
        if (customerRepository.findCustomerByEmailIgnoreCase(dto.email()).isPresent()) {
            throw new CustomerServiceException(ErrorType.EMAIL_ALREADY_EXISTS);
        }
        customerRepository.save(Customer.builder().memberId(2L).firstName(dto.firstName()).lastName(dto.lastName()).email(dto.email()).phone(dto.phone()).address(dto.address()).status(EStatus.ACTIVE).build());
    }

    // This method will return members customers with paginable
    public List<Customer> findAll(PageRequestDTO dto) {
        return customerRepository.findAllByFirstNameContainingIgnoreCaseAndStatusAndMemberIdOrderByFirstNameAsc(dto.searchText(), EStatus.ACTIVE, SessionManager.getMemberIdFromAuthenticatedMember(), PageRequest.of(dto.page(), dto.size()));

    }

    // This method will update customer by token //TEST
    public Boolean update(CustomerUpdateDTO customerUpdateDTO) {
        Customer customer = customerRepository.findById(customerUpdateDTO.id()).orElseThrow(() -> new CustomerServiceException(ErrorType.NOT_FOUNDED_CUSTOMER));
        SessionManager.authorizationCheck(customer.getMemberId());
        if (customer.getStatus() != EStatus.DELETED && customer.getStatus() != EStatus.PENDING) {
            customer.setFirstName(customerUpdateDTO.firstName() != null ? customerUpdateDTO.firstName() : customer.getFirstName());
            customer.setLastName(customerUpdateDTO.lastName() != null ? customerUpdateDTO.lastName() : customer.getLastName());
            customer.setPhone(customerUpdateDTO.phone() != null ? customerUpdateDTO.phone() : customer.getPhone());
            customer.setAddress(customerUpdateDTO.address() != null ? customerUpdateDTO.address() : customer.getAddress());
            customer.setEmail(customerUpdateDTO.email() != null ? customerUpdateDTO.email() : customer.getEmail());
            customerRepository.save(customer);
            return true;
        } else {
            throw new CustomerServiceException(ErrorType.CUSTOMER_NOT_ACTIVE);
        }
    }

    // This method will delete customer by token
    public Boolean delete(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new CustomerServiceException(ErrorType.NOT_FOUNDED_CUSTOMER));
        SessionManager.authorizationCheck(customer.getMemberId());
        if (customer.getStatus() == EStatus.DELETED) {
            throw new CustomerServiceException(ErrorType.CUSTOMER_ALREADY_DELETED);
        }
        customer.setStatus(EStatus.DELETED);
        customerRepository.save(customer);
        return true;
    }


    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerServiceException(ErrorType.NOT_FOUNDED_CUSTOMER));
    }

    public List<CustomerResponseForOpportunityDTO> getAllCustomersForOpportunity(PageRequestDTO dto) {
        return customerRepository.findAllByFirstNameContainingIgnoreCaseAndMemberIdOrderByFirstNameAsc(dto.searchText(), SessionManager.getMemberIdFromAuthenticatedMember(), PageRequest.of(dto.page(), dto.size()));
    }
    public List<OpportunityResponseDTO> getAllCustomersForOpportunity() {
        List<Customer> customers = customerRepository.findByStatus(EStatus.ACTIVE);
        return customers.stream().map(customer -> new OpportunityResponseDTO(customer.getId(), customer.getFirstName(), customer.getLastName())).collect(Collectors.toList());
    }
    public List<Customer> findAllByIds(List<Long> ids) {
        return customerRepository.findAllById(ids);
    }

    public Boolean uploadExcelCustomers(AllCustomerSaveDTO dtoList) {
        List<Customer> customers = dtoList.customers().stream().map(dto -> {
            Customer customer = new Customer();
            customer.setFirstName(dto.firstName());
            customer.setLastName(dto.lastName());
            customer.setEmail(dto.email());
            customer.setPhone(dto.phone());
            customer.setAddress(dto.address());
            customer.setMemberId(SessionManager.getMemberIdFromAuthenticatedMember());
            customer.setStatus(EStatus.ACTIVE);
            return customer;
        }).collect(Collectors.toList());
        customerRepository.saveAll(customers);
        return true;
    }

}
