/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.models.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.common.CodeGenerator;
import org.hisp.dhis.client.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Event implements Serializable, IdentifiableObject {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FUTURE_VISIT = "SCHEDULE";
    public static final String STATUS_SKIPPED = "SKIPPED";

    @JsonIgnore
    private long id;

    @JsonProperty("event")
    private String eventUid;

    @JsonProperty("status")
    private String status;

    @JsonIgnore
    private Double latitude;

    @JsonIgnore
    private Double longitude;

    @JsonIgnore
    private TrackedEntityInstance trackedEntityInstance;

    @JsonIgnore
    private Enrollment enrollment;

    @JsonProperty("program")
    private String programId;

    @JsonProperty("programStage")
    private String programStageId;

    @JsonProperty("orgUnit")
    private String organisationUnitId;

    @JsonProperty("eventDate")
    private DateTime eventDate;

    @JsonProperty("dueDate")
    private DateTime dueDate;

    @JsonProperty("name")
    private String name;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("created")
    private DateTime created;

    @JsonProperty("lastUpdated")
    private DateTime lastUpdated;

    @JsonProperty("access")
    private Access access;

    @JsonProperty("dataValues")
    private List<TrackedEntityDataValue> trackedEntityDataValues;

    public Event() {

    }

    public static Event create() {
        Event event = new Event();
        event.eventUid = CodeGenerator.generateCode();
        return event;
    }

    public static Event create(String organisationUnitId, String programId, String
            programStageId, String eventStatus) {
        Event event = new Event();
        event.setUId(CodeGenerator.generateCode());
        event.setOrganisationUnitId(organisationUnitId);
        event.setProgramId(programId);
        event.setProgramStageId(programStageId);
        event.setStatus(eventStatus);
        return event;
    }

    public static Event create(String organisationUnitUId, String programUId, String enrollmentUId,
                               String trackedEntityInstanceUId, String programStageUId,
                               String eventStatus) {
        Event event = new Event();
        event.setUId(CodeGenerator.generateCode());
        event.setOrganisationUnitId(organisationUnitUId);
        event.setEnrollmentUid(enrollmentUId);
        event.setTrackedEntityInstanceUid(trackedEntityInstanceUId);
        event.setProgramId(programUId);
        event.setProgramStageId(programStageUId);
        event.setStatus(eventStatus);
        return event;
    }

    @JsonProperty("coordinate")
    public Map<String, Object> getCoordinate() {
        Map<String, Object> coordinate = new HashMap<>();
        coordinate.put("latitude", latitude);
        coordinate.put("longitude", longitude);
        return coordinate;
    }

    @JsonProperty("coordinate")
    public void setCoordinate(Map<String, Object> coordinate) {
        this.latitude = (double) coordinate.get("latitude");
        this.longitude = (double) coordinate.get("longitude");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("trackedEntityInstance")
    public String getTrackedEntityInstanceUid() {
        return trackedEntityInstance.getTrackedEntityInstanceUid();
    }

    @JsonProperty("trackedEntityInstance")
    public void setTrackedEntityInstanceUid(String trackedEntityInstanceUid) {
        this.trackedEntityInstance = new TrackedEntityInstance();
        this.trackedEntityInstance.setTrackedEntityInstanceUid(trackedEntityInstanceUid);
    }

    @JsonProperty("enrollment")
    public String getEnrollmentUid() {
        return enrollment.getUId();
    }

    @JsonProperty("enrollment")
    public void setEnrollmentUid(String enrollmentUid) {
        this.enrollment = new Enrollment();
        this.enrollment.setUId(enrollmentUid);
    }

    @Override
    public String getUId() {
        return eventUid;
    }

    @Override
    public void setUId(String uId) {
        this.eventUid = uId;
    }

    public TrackedEntityInstance getTrackedEntityInstance() {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance(TrackedEntityInstance trackedEntityInstance) {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProgramStageId() {
        return programStageId;
    }

    public void setProgramStageId(String programStageId) {
        this.programStageId = programStageId;
    }

    public String getOrganisationUnitId() {
        return organisationUnitId;
    }

    public void setOrganisationUnitId(String organisationUnitId) {
        this.organisationUnitId = organisationUnitId;
    }

    public DateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(DateTime eventDate) {
        this.eventDate = eventDate;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(DateTime dueDate) {
        this.dueDate = dueDate;
    }

    public List<TrackedEntityDataValue> getTrackedEntityDataValues() {
        return trackedEntityDataValues;
    }

    public void setTrackedEntityDataValues(List<TrackedEntityDataValue> trackedEntityDataValues) {
        this.trackedEntityDataValues = trackedEntityDataValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    /**
     * Comparator that returns the Event with the latest EventDate as the greater of the two given.
     */
    public static class EventDateComparator implements Comparator<Event> {

        @Override
        public int compare(Event first, Event second) {
            if (first == null && second == null) {
                return 0;
            } else if (first == null) {
                return -1;
            } else if (second == null) {
                return 1;
            }

            DateTime firstDate = new DateTime(first.getEventDate());
            DateTime secondDate = new DateTime(second.getEventDate());

            if (firstDate.isBefore(secondDate)) {
                return -1;
            } else if (firstDate.isAfter(secondDate)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}