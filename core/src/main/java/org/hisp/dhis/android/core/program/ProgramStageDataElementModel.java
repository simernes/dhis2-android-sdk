package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class ProgramStageDataElementModel extends BaseIdentifiableObjectModel {

    public interface Columns extends BaseIdentifiableObjectModel.Columns {
        String DISPLAY_IN_REPORTS = "displayInReports";
        String COMPULSORY = "compulsory";
        String ALLOW_PROVIDED_ELSEWHERE = "allowProvidedElsewhere";
        String SORT_ORDER = "sortOrder";
        String ALLOW_FUTURE_DATE = "allowFutureDate";
        String DATA_ELEMENT = "dataElement";
        String PROGRAM_STAGE_SECTION = "programStageSection";
    }

    @Nullable
    @ColumnName(Columns.DISPLAY_IN_REPORTS)
    public abstract Boolean displayInReports();

    @Nullable
    @ColumnName(Columns.COMPULSORY)
    public abstract Boolean compulsory();

    @Nullable
    @ColumnName(Columns.ALLOW_PROVIDED_ELSEWHERE)
    public abstract Boolean allowProvidedElsewhere();

    @Nullable
    @ColumnName(Columns.SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @ColumnName(Columns.ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE_SECTION)
    public abstract String programStageSection();

    public static ProgramStageDataElementModel create(Cursor cursor) {
        return AutoValue_ProgramStageDataElementModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramStageDataElementModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder displayInReports(@Nullable Boolean displayInReports);

        public abstract Builder compulsory(@Nullable Boolean compulsory);

        public abstract Builder allowProvidedElsewhere(@Nullable Boolean allowProvidedElsewhere);

        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        public abstract Builder allowFutureDate(@Nullable Boolean allowFutureDate);

        public abstract Builder dataElement(@Nullable String dataElement);

        public abstract Builder programStageSection(@Nullable String programStageSection);

        public abstract ProgramStageDataElementModel build();
    }

}