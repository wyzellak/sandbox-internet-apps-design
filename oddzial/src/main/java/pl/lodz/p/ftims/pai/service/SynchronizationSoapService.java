package pl.lodz.p.ftims.pai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.lodz.p.ftims.pai.config.EnvironmentConfiguration;
import pl.lodz.p.ftims.pai.web.soap.SynchronizationResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

/**
 * Created by <a href="mailto:171131@edu.p.lodz.pl">Andrzej Lisowski</a> on 24.01.16.
 */
@Component
public class SynchronizationSoapService {
    private static final String SERVER_URI = "http://osemka.com";
    private static final String XML_PREFIX = "gs";

    private static final Logger LOG = LoggerFactory.getLogger(SynchronizationSoapService.class);

    @Autowired
    private EnvironmentConfiguration environmentConfiguration;

    public SynchronizationResponse sendSynchronizationRequest() throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        String synchronizationEndpoint = getSynchronizationEndpoint();
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), synchronizationEndpoint);

        soapConnection.close();

        return transformSoapResponseToSynchronizationResponse(soapResponse);
    }

    private SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(XML_PREFIX, SERVER_URI);

        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("synchronizationRequest", XML_PREFIX);
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("department", XML_PREFIX);
        soapBodyElem1.addTextNode(String.valueOf(environmentConfiguration.getDepartmentId()));

        soapMessage.saveChanges();

        LOG.debug("Request SOAP message: {}", soapMessage);

        return soapMessage;
    }

    private String getSynchronizationEndpoint() {
        return "http://"+environmentConfiguration.getHeadquartersDomain()+":"+ environmentConfiguration.getHeadquartersPort()+ environmentConfiguration.getHeadquartersSynchronizationEndpoint();
    }

    private SynchronizationResponse transformSoapResponseToSynchronizationResponse(SOAPMessage soapResponse) throws Exception {
        Source sourceContent = soapResponse.getSOAPPart().getContent();

        XMLInputFactory xif = XMLInputFactory.newFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(sourceContent);
        xsr.nextTag(); // Advance to Envelope tag
        while (!xsr.getLocalName().equals("synchronizationResponse")) {
            xsr.nextTag();
        }

        JAXBContext jc = JAXBContext.newInstance(SynchronizationResponse.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<SynchronizationResponse> je = unmarshaller.unmarshal(xsr, SynchronizationResponse.class);

        return je.getValue();
    }
}