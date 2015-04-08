/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis2.android.sdk.controllers.metadata;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.otto.Subscribe;

import org.hisp.dhis2.android.sdk.controllers.Dhis2;
import org.hisp.dhis2.android.sdk.events.MetaDataResponseEvent;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.persistence.models.Constant;
import org.hisp.dhis2.android.sdk.persistence.models.Constant$Table;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement$Table;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet$Table;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnit$Table;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnitProgramRelationship;
import org.hisp.dhis2.android.sdk.persistence.models.OrganisationUnitProgramRelationship$Table;
import org.hisp.dhis2.android.sdk.persistence.models.Program;
import org.hisp.dhis2.android.sdk.persistence.models.Program$Table;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramIndicator;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramIndicator$Table;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStage;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStage$Table;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramTrackedEntityAttribute$Table;
import org.hisp.dhis2.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttribute$Table;
import org.hisp.dhis2.android.sdk.persistence.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 19.02.15.
 */
public class MetaDataController {
    private final static String CLASS_TAG = "MetaDataController";


    private MetaDataLoader metaDataLoader;

    public MetaDataController() {
        Dhis2Application.bus.register(this);
        metaDataLoader = new MetaDataLoader();
    }

    /**
     * Returns a list of programs assigned to the given organisation unit id
     * @param organisationUnitId
     * @param kinds set to null to get all programs. Else get kinds Strings from Program.
     * @return
     */
    public static List<Program> getProgramsForOrganisationUnit(String organisationUnitId,
                                                               String... kinds) {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships =
                Select.all(OrganisationUnitProgramRelationship.class,
                        Condition.column(OrganisationUnitProgramRelationship$Table.ORGANISATIONUNITID).
                                is(organisationUnitId));

        List<Program> programs = new ArrayList<Program>();
        for(OrganisationUnitProgramRelationship oupr: organisationUnitProgramRelationships ) {
            //List<Condition> conditions = new ArrayList<Condition>();
            //conditions.add(Condition.column(Program$Table.ID).is(oupr.programId));
            if(kinds!=null) {
                for(String kind: kinds)
                {
                    //conditions.add(Condition.column(Program$Table.KIND).is(kind));
                    List<Program> plist = new Select().from(Program.class).where(
                            Condition.column(Program$Table.ID).is(oupr.programId)).and(
                            Condition.column(Program$Table.KIND).is(kind)).queryList();
                    programs.addAll(plist);
                }
            }
        }
        return programs;
    }

    public static List<ProgramStage> getProgramStages(String program) {
        return new Select().from(ProgramStage.class).where(
                Condition.column(ProgramStage$Table.PROGRAM).is(program)).orderBy(
                ProgramStage$Table.SORTORDER).queryList();
    }

    /**
     * Returns a program stage for a given program stage uid
     * @param programStageUid
     * @return
     */
    public static ProgramStage getProgramStage(String programStageUid) {
        return new Select().from(ProgramStage.class).where(
                Condition.column(ProgramStage$Table.ID).is(programStageUid)).querySingle();
    }

    /**
     * todo: programTrackedEntityAttributes is not necessarily unique for a trackedEntityAttribute.
     * todo: implement program parameter
     * @param trackedEntityAttribute
     * @return
     */
    public static ProgramTrackedEntityAttribute getProgramTrackedEntityAttribute(String trackedEntityAttribute) {
        return new Select().from(ProgramTrackedEntityAttribute.class).where
                (Condition.column(ProgramTrackedEntityAttribute$Table.TRACKEDENTITYATTRIBUTE).is
                        (trackedEntityAttribute)).querySingle();
    }

    public static TrackedEntityAttribute getTrackedEntityAttribute(String trackedEntityAttributeId) {
        return new Select().from(TrackedEntityAttribute.class).where(Condition.column
                (TrackedEntityAttribute$Table.ID).is(trackedEntityAttributeId)).querySingle();
    }

    /**
     * Returns a constant with the given uid
     * @param id
     * @return
     */
    public static Constant getConstant(String id) {
        return new Select().from(Constant.class).where
                (Condition.column(Constant$Table.ID).is(id)).querySingle();
    }

    /**
     * returns a list of all constants
     * @return
     */
    public static List<Constant> getConstants() {
        return Select.all(Constant.class);
    }

    /**
     * Returns a list of IDs for all assigned programs.
     * @return
     */
    public static List<String> getAssignedPrograms() {
        List<OrganisationUnitProgramRelationship> organisationUnitProgramRelationships = Select.all(OrganisationUnitProgramRelationship.class);
        List<String> assignedPrograms = new ArrayList<>();
        for(OrganisationUnitProgramRelationship relationship: organisationUnitProgramRelationships) {
            if(!assignedPrograms.contains(relationship.programId)) assignedPrograms.add(relationship.programId);
        }
        return assignedPrograms;
    }

    public static OrganisationUnit getOrganisationUnit(String id) {
        return new Select().from(OrganisationUnit.class).where(Condition.column(OrganisationUnit$Table.ID).is(id)).querySingle();
    }

    public static SystemInfo getSystemInfo() {
        List<SystemInfo> result = Select.all(SystemInfo.class);
        if(result != null && result.size() > 0) return result.get(0);
        else return null;
    }

    public static Program getProgram(String programId) {
        List<Program> plist = Select.all(Program.class, Condition.column(Program$Table.ID).
                is(programId));
        if(plist != null && plist.size() > 0) return plist.get(0);
        else return null;
    }

    /**
     * Returns a list of organisation units assigned to the current user
     * @return
     */
    public static List<OrganisationUnit> getAssignedOrganisationUnits() {
        List<OrganisationUnit> organisationUnits = Select.all(OrganisationUnit.class);
        return organisationUnits;
    }

    /**
     * Returns the data element for the given uid or null if the dataElement does not exist
     * @param dataElementId
     * @return
     */
    public static DataElement getDataElement(String dataElementId) {
        List<DataElement> result =
            Select.all(DataElement.class, Condition.column(DataElement$Table.ID).is(dataElementId));
        if(result != null && result.size() > 0)
            return result.get(0);
        else return null;
    }

    public static User getUser() {
        List<User> users = Select.all(User.class);
        if (users.size() == 0) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Returns an option set for the given Id or null of the option set doesn't exist.
     * @param optionSetId
     * @return
     */
    public static OptionSet getOptionSet(String optionSetId) {
        List<OptionSet> result = Select.all(OptionSet.class, Condition.column(OptionSet$Table.ID).
                is(optionSetId));
        if(result!=null && result.size() > 0)
            return result.get(0);
        else return null;
    }

    public static List<ProgramIndicator> getProgramIndicatorsByProgram(String program) {
        return Select.all(ProgramIndicator.class, Condition.column(ProgramIndicator$Table.PROGRAM).is(program));
    }

    public static List<ProgramIndicator> getProgramIndicatorsByProgramStage(String programStage) {
        return Select.all(ProgramIndicator.class,
                Condition.column(ProgramIndicator$Table.PROGRAMSTAGE).is(programStage));
    }

    public static List<ProgramIndicator> getProgramIndicatorsBySection(String section) {
        return Select.all(ProgramIndicator.class,
                Condition.column(ProgramIndicator$Table.SECTION).is(section));
    }

    public void synchronizeMetaData(Context context) {
        metaDataLoader.synchronizeMetaData(context);
    }

    /**
     * Initiates loading of metadata from the server
     * @param context
     */
    public void loadMetaData(Context context) {
        metaDataLoader.loadMetaData(context);
    }

    /**
     * Resets the value set for last updated
     * @param context
     */
    public void resetLastUpdated(Context context) {
        metaDataLoader.resetLastUpdated(context);
    }

    public void clearMetaDataLoadedFlags(Context context) {
        metaDataLoader.clearMetaDataLoadedFlags(context);
    }

    @Subscribe
    public void onResponse(MetaDataResponseEvent event) {
        Log.e(CLASS_TAG, "onResponse");
    }

    public boolean isLoading() {
        return metaDataLoader.loading;
    }

    public boolean isSynchronizing() {
        return metaDataLoader.synchronizing;
    }
}