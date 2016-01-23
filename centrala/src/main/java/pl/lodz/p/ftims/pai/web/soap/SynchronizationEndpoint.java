package pl.lodz.p.ftims.pai.web.soap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.lodz.p.ftims.pai.repository.DepartmentRepository;
import pl.lodz.p.ftims.pai.repository.EmployeeRepository;
import pl.lodz.p.ftims.pai.repository.TransporterRepository;

@Endpoint
public class SynchronizationEndpoint {
    private static final String NAMESPACE_URI = "http://osemka.com";

    @Autowired
    private TransporterRepository transporterRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "synchronizationRequest")
    @ResponsePayload
    public SynchronizationResponse getCountry(@RequestPayload SynchronizationRequest request) {
        long departmentId = request.getDepartment();

        SynchronizationResponse response = new SynchronizationResponse();

        response.getTransporter().addAll(transporterRepository.findAll());
        response.setDepartment(departmentRepository.findAll());
        response.setEmployee(employeeRepository.findByDepartmentId(departmentId));

        return response;
    }
}