/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.macys.platform.providers.datarepair.test.performance.invoker;


import com.macys.platform.integration.availabilityupdate.dto.AvailabilityUpdateDTO;
import com.macys.platform.integration.datarepair.DataRepairRequestDTO;

import java.util.*;

public class HandleUpdateAvailabilityCatalogDataRepairServiceQueryProvider {

    private List<String> upcIds;

    private Boolean available;

    private List<String> options;

    public List<AvailabilityUpdateDTO> queries() {

        final AvailabilityUpdateDTO availabilityUpdateDTO = new AvailabilityUpdateDTO();

        Map<String, Boolean> upcAvailabilities = new HashMap<String, Boolean>();

        for (String upcId : upcIds) {

            upcAvailabilities.put(upcId, available);
        }

        availabilityUpdateDTO.setUpcAvailabilities(upcAvailabilities);

        Set<DataRepairRequestDTO.Option> optSet = new LinkedHashSet<DataRepairRequestDTO.Option>();
        for (String option : options) {
            optSet.add(DataRepairRequestDTO.Option.valueOf(option));
        }
        availabilityUpdateDTO.setOptions(optSet);

        return Collections.singletonList(availabilityUpdateDTO);
    }

    public void setUpcIds(List<String> upcIds) {
        this.upcIds = upcIds;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
