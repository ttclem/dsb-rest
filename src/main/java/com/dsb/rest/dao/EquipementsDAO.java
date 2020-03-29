package com.dsb.rest.dao;

import com.dsb.rest.model.Equipments;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Repository
public class EquipementsDAO {
    private static final int pageSize = 50;

    private List<Equipments> equipmentsListSort = null;

    private List<Equipments> deserialized(String dir) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Equipments[] equipments = objectMapper.readValue(new FileReader(dir), Equipments[].class);
        return Arrays.asList(equipments);
    }

    public List<Equipments> getAllEquipements(String dir, int page) throws IOException {
        List<Equipments> equipmentsList = getEquipmentsList(dir);

        equipmentsList = equipmentsList.stream().sorted(Comparator.comparingInt((Equipments e) -> Integer.parseInt(e.getLvl()))).collect(toListReversed());
        return equipmentsList.stream().skip(pageSize * (page - 1)).limit(pageSize).collect(Collectors.toList());
    }

    public Integer getTotalEquipement(String dir) throws IOException {
        List<Equipments> equipmentsList = getEquipmentsList(dir);
        return equipmentsList.size();
    }

    public List<Equipments> filterEquipementsByLevel(String dir, int page, int level) throws IOException {
        List<Equipments> equipmentsList = getEquipmentsList(dir);

        equipmentsListSort = equipmentsList
                .stream()
                .filter(equipments -> Integer.parseInt(equipments.getLvl()) <= level)
                .skip(pageSize * (page - 1))
                .limit(pageSize)
                .collect(Collectors.toList());

        return equipmentsListSort;
    }

    public List<Equipments> filterEquipementsByName(String dir, int page, String name) throws IOException {
        List<Equipments> equipmentsList = getEquipmentsList(dir);

        equipmentsListSort = equipmentsList
                .stream()
                .filter(equipments -> Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE).matcher(equipments.getName()).find())
                .skip(pageSize * (page - 1))
                .limit(pageSize)
                .collect(Collectors.toList());

        return equipmentsListSort;
    }

    private static <T> Collector<T, ?, List<T>> toListReversed() {
        return Collectors.collectingAndThen(Collectors.toList(), l -> {
            Collections.reverse(l);
            return l;
        });
    }

    private List<Equipments> getEquipmentsList(String dir) throws IOException {
        List<Equipments> equipmentsList;
        if (equipmentsListSort == null) {
            equipmentsList = this.deserialized(dir);
        } else {
            equipmentsList = equipmentsListSort;
        }
        return equipmentsList;
    }
}
