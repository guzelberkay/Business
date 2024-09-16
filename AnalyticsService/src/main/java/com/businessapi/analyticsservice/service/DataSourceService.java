package com.businessapi.analyticsservice.service;

import com.businessapi.analyticsservice.dto.request.DataSourceRequestDto;
import com.businessapi.analyticsservice.dto.response.DataSourceResponseDto;
import com.businessapi.analyticsservice.entity.DataSource;
import com.businessapi.analyticsservice.exception.AnalyticsServiceAppException;
import com.businessapi.analyticsservice.exception.ErrorType;
import com.businessapi.analyticsservice.mapper.DataSourceMapper;
import com.businessapi.analyticsservice.repository.DataSourceRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DataSourceService {

    private final DataSourceRepository dataSourceRepository;
    private final DataSourceMapper dataSourceMapper;
    private RestTemplate restTemplate;

    public DataSourceService(DataSourceRepository dataSourceRepository, DataSourceMapper dataSourceMapper, RestTemplate restTemplate) {
        this.dataSourceRepository = dataSourceRepository;
        this.dataSourceMapper = dataSourceMapper;
        this.restTemplate = restTemplate;
    }

    // Fetch serviceType data from Rest API and save it to database
    public void fetchDataAndSave(String serviceType, String endpointType) {
        String url = getUrlByServiceTypeAndEndpoint(serviceType, endpointType);

        if (url == null) {
            throw new IllegalArgumentException("Invalid service or endpoint type: " + serviceType + ", " + endpointType);
        }

        String jsonData;

        // Used for GET request
        if (serviceType.equalsIgnoreCase("hrm")) {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                jsonData = response.getBody();
            } else {
                throw new RuntimeException("Data fetch failed with status: " + response.getStatusCode());
            }
        } else {
            // For Stock and Finance, used POST request with request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("searchText", "");
            requestBody.put("page", 0);
            requestBody.put("size", 100);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                jsonData = response.getBody();
            } else {
                throw new RuntimeException("Data fetch failed with status: " + response.getStatusCode());
            }
        }
        System.out.println(jsonData);
        dataSourceRepository.deleteByEndpointType(endpointType); // delete old data
        saveDataSource(endpointType, serviceType, jsonData);
    }

    private String getUrlByServiceTypeAndEndpoint(String serviceType, String endpointType) {
        switch (serviceType.toLowerCase()) {
            case "stock":
                return getStockServiceUrl(endpointType);
            case "finance":
                return getFinanceServiceUrl(endpointType);
            case "hrm":
                return getHrmServiceUrl(endpointType);
            default:
                return null;
        }
    }

    private String getStockServiceUrl(String endpointType) {
        switch (endpointType.toLowerCase()) {
            case "product":
                return "http://localhost:9099/dev/v1/product/find-all";
            case "order":
                return "http://localhost:9099/dev/v1/order/find-all";
            case "stock-movement":
                return "http://localhost:9099/dev/v1/stock-movement/find-all";
            case "supplier":
                return "http://localhost:9099/dev/v1/supplier/find-all";
            case "ware-house":
                return "http://localhost:9099/dev/v1/ware-house/find-all";
            default:
                return null;
        }
    }

    private String getFinanceServiceUrl(String endpointType) {
        switch (endpointType.toLowerCase()) {
            case "tax":
                return "http://localhost:9089/tax/find-all";
            case "invoice":
                return "http://localhost:9089/invoice/find-all";
            case "financial-report":
                return "http://localhost:9089/financial-report/find-all";
            case "expense":
                return "http://localhost:9089/expense/find-all";
            case "budget":
                return "http://localhost:9089/budget/find-all";
            default:
                return null;
        }
    }

    private String getHrmServiceUrl(String endpointType) {
        // New HRMService URLs
        switch (endpointType.toLowerCase()) {
            case "performance":
                return "http://localhost:9096/dev/v1/performance/find-all";
            case "payroll":
                return "http://localhost:9096/dev/v1/payroll/find-all";
            case "employee":
                return "http://localhost:9096/dev/v1/employee/find-all";
            case "benefit":
                return "http://localhost:9096/dev/v1/benefit/find-all";
            case "attendance":
                return "http://localhost:9096/dev/v1/attendance/find-all";
            default:
                return null;
        }
    }

    private void saveDataSource(String endpointType, String serviceType ,String data) {
        DataSource dataSource = DataSource.builder()
                .endpointType(endpointType)
                .serviceType(serviceType)
                .data(data)
                .build();
        System.out.println(data);
        dataSourceRepository.save(dataSource);
    }

    public DataSourceResponseDto createDataSource(DataSourceRequestDto dataSourceRequestDto) {
        DataSource dataSource = dataSourceMapper.toEntity(dataSourceRequestDto);
        dataSource = dataSourceRepository.save(dataSource);
        return dataSourceMapper.toDto(dataSource);
    }

    public List<DataSourceResponseDto> getAllDataSources() {
        return dataSourceMapper.toDtoList(dataSourceRepository.findAll());
    }

    public DataSourceResponseDto getDataSourceById(Long id) {
        DataSource dataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new AnalyticsServiceAppException(ErrorType.DATA_NOT_FOUND, "DataSource not found for id: " + id));
        return dataSourceMapper.toDto(dataSource);
    }

    public void deleteDataSource(Long id) {
        if (!dataSourceRepository.existsById(id)) {
            throw new AnalyticsServiceAppException(ErrorType.DATA_NOT_FOUND, "DataSource not found for id: " + id);
        }
        dataSourceRepository.deleteById(id);
    }
}
