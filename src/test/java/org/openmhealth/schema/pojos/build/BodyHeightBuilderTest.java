package org.openmhealth.schema.pojos.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.joda.time.DateTime;
import org.junit.Test;
import org.openmhealth.schema.pojos.BodyHeight;
import org.openmhealth.schema.pojos.BodyWeight;
import org.openmhealth.schema.pojos.generic.LengthUnitValue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

import static org.junit.Assert.*;
import static org.openmhealth.schema.pojos.generic.LengthUnitValue.*;
import static org.openmhealth.schema.pojos.generic.LengthUnitValue.LengthUnit.*;
import static org.openmhealth.schema.pojos.generic.MassUnitValue.MassUnit.lb;

public class BodyHeightBuilderTest {

    @Test
        public void test() throws IOException, ProcessingException {

            final String BODY_HEIGHT_SCHEMA = "schemas/body-height-1.0.json";

            URL url = Thread.currentThread().getContextClassLoader().getResource(BODY_HEIGHT_SCHEMA);
            assertNotNull(url);

            ObjectMapper mapper = new ObjectMapper();

            InputStream inputStream = url.openStream();
            JsonNode schemaNode = mapper.readTree(inputStream);

            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            final JsonSchema schema = factory.getJsonSchema(schemaNode);

            ProcessingReport report;

            BodyHeightBuilder builder = new BodyHeightBuilder();

            BodyHeight invalidBodyHeight = builder.build();
            String invalidJson = mapper.writeValueAsString(invalidBodyHeight);

            report = schema.validate(mapper.readTree(invalidJson));
            System.out.println(report);
            assertFalse("Expected invalid result but got success", report.isSuccess());

            builder.setHeight(150d, cm);
            builder.setTimeTaken(new DateTime());
            BodyHeight bodyHeight = builder.build();

            assertNotNull(bodyHeight.getEffectiveTimeFrame());
            assertNotNull(bodyHeight.getLengthUnitValue());
            assertEquals(bodyHeight.getLengthUnitValue().getUnit(),cm);
            assertEquals(bodyHeight.getLengthUnitValue().getValue(),new BigDecimal(150d));

            String rawJson = mapper.writeValueAsString(bodyHeight);

            report = schema.validate(mapper.readTree(rawJson));
            System.out.println(report);

            assertTrue("Expected valid result!", report.isSuccess());
        }

}
