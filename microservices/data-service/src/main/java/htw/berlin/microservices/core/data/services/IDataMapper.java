package htw.berlin.microservices.core.data.services;

import htw.berlin.api.core.data.Data;
import htw.berlin.api.core.data.ModuleGrade;
import htw.berlin.api.core.data.Transcript;
import htw.berlin.microservices.core.data.persistence.DataEntity;
import htw.berlin.microservices.core.data.persistence.ModuleGradeObj;
import htw.berlin.microservices.core.data.persistence.TranscriptObj;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface IDataMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Data entityToApi(DataEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    DataEntity apiToEntity(Data api);

    Transcript transcriptObjToTranscript(TranscriptObj transcriptObj);
    TranscriptObj transcriptToTranscriptObj(Transcript transcript);

    ModuleGrade moduleGradeObjToModuleGrade(ModuleGradeObj moduleGradeObj);
    ModuleGradeObj moduleGradeToModuleGradeObj(ModuleGrade moduleGrade);

}
