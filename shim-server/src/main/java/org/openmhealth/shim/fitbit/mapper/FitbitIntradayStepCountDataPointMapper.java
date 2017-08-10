/*
 * Copyright 2015 Open mHealth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openmhealth.shim.fitbit.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.openmhealth.schema.domain.omh.DataPoint;
import org.openmhealth.schema.domain.omh.StepCount1;

import java.math.BigDecimal;
import java.util.Optional;

import static org.openmhealth.shim.common.mapper.JsonNodeMappingSupport.asRequiredBigDecimal;


/**
 * A mapper that translates responses from the Fitbit Resource API <code>activities/steps</code> endpoint into {@link
 * StepCount1} data points. This mapper assumes one minute granularity, i.e. that the request specified a
 * <code>detail-level</code> of <code>1min</code>.
 *
 * @author Chris Schaefbauer
 * @see <a href="https://dev.fitbit.com/docs/activity/#get-activity-intraday-time-series">API documentation</a>
 */
public class FitbitIntradayStepCountDataPointMapper extends FitbitIntradayDataPointMapper<StepCount1> {

    @Override
    protected String getListNodeName() {
        return "activities-steps-intraday.dataset";
    }

    @Override
    public String getSummaryForDayNodeName() {
        return "activities-steps";
    }

    @Override
    protected Optional<DataPoint<StepCount1>> asDataPoint(JsonNode listEntryNode) {

        BigDecimal stepCountValue = asRequiredBigDecimal(listEntryNode, "value");

        if (stepCountValue.intValue() == 0) {
            return Optional.empty();
        }

        StepCount1.Builder stepCountBuilder = new StepCount1.Builder(stepCountValue);

        setEffectiveTimeFrameFromTimeSeriesElementTimestamp(listEntryNode, stepCountBuilder);

        return Optional.of(
                newDataPoint(stepCountBuilder.build(), getExternalIdFromTimeSeriesElementTimestamp(listEntryNode)));
    }
}
