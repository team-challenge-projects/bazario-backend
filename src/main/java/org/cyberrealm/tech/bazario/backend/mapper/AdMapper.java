package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.ad.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AdMapper {
    AdDto toDto(Ad ad);

    Ad toEntity(CreateAdRequestDto requestDto);
}
