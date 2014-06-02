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
package com.griddynamics.jagger.engine.e1.reporting;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.dbapi.entity.WorkloadData;
import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import com.griddynamics.jagger.util.Decision;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Required;

import java.awt.*;
import java.util.List;

public class WorkloadReporter extends AbstractReportProvider {
    private SessionStatusDecisionMaker decisionMaker;
    private StatusImageProvider statusImageProvider;

	@Override
	public JRDataSource getDataSource() {
		@SuppressWarnings("unchecked")
		List<WorkloadData> testData = getHibernateTemplate().find("from WorkloadData d where d.sessionId=? order by d.number asc, d.scenario.name asc",
                getSessionIdProvider().getSessionId());

		@SuppressWarnings("unchecked")
		List<WorkloadTaskData> allWorkloadTasks = getHibernateTemplate().find("from WorkloadTaskData d where d.sessionId=? order by d.number asc, d.scenario.name asc",
                getSessionIdProvider().getSessionId());

        @SuppressWarnings({"unchecked"}) List<TaskData> taskDatas = getHibernateTemplate().find(
                "select d from TaskData d where d.sessionId=?",
                getSessionIdProvider().getSessionId());


		List<E1ScenarioReportData> result = Lists.newLinkedList();

		for (WorkloadData workloadData : testData) {
			E1ScenarioReportData reportData = new E1ScenarioReportData();
			reportData.setSessionId(workloadData.getSessionId());
            reportData.setNumber(workloadData.getNumber().toString());
			reportData.setScenarioName(workloadData.getScenario().getName());

			WorkloadTaskData resultData = null;
			for (WorkloadTaskData workloadTaskData : allWorkloadTasks) {
				if (workloadTaskData.getTaskId().equals(workloadData.getTaskId())) {
					resultData = workloadTaskData;
					break;
				}
			}

			if (resultData == null) {
				throw new IllegalStateException("Result data is not specified");
			}

            reportData.setStatusImage(statusImageProvider.getImageByDecision(decisionMaker.decideOnTest(resultData)));

            for(TaskData taskData: taskDatas) {
                if(workloadData.getTaskId().equals(taskData.getTaskId())) {
                    if(TaskData.ExecutionStatus.FAILED.equals(taskData.getStatus())) {
                        reportData.setStatusImage(statusImageProvider.getImageByDecision(Decision.ERROR));
                    }
                    reportData.setId(taskData.getId().toString());
                }
            }


			result.add(reportData);
		}

		return new JRBeanCollectionDataSource(result);
	}

    @Required
    public void setDecisionMaker(SessionStatusDecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    public void setStatusImageProvider(StatusImageProvider statusImageProvider) {
        this.statusImageProvider = statusImageProvider;
    }

    public static class E1ScenarioReportData {
		private String sessionId;
        private String number;
		private String scenarioName;
        private String Id;

        private Image statusImage;

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}


        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getScenarioName() {
			return scenarioName;
		}

		public void setScenarioName(String scenarioName) {
			this.scenarioName = scenarioName;
		}

        public Image getStatusImage() {
            return statusImage;
        }

        public void setStatusImage(Image statusImage) {
            this.statusImage = statusImage;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }
    }
}
