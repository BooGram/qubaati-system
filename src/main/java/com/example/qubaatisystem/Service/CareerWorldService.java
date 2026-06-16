package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.CareerWorldInDTO;
import com.example.qubaatisystem.DTO.Out.CareerWorldOutDTO;
import com.example.qubaatisystem.Model.CareerWorld;
import com.example.qubaatisystem.Repository.CareerWorldRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CareerWorldService {

    private final CareerWorldRepository careerWorldRepository;
    private final ModelMapper modelMapper;

    public List<CareerWorldOutDTO> getAll() {
        return careerWorldRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public CareerWorldOutDTO getById(Integer id) {
        List<CareerWorld> careerWorlds = careerWorldRepository.findCareerWorldById(id);
        if (careerWorlds.isEmpty()) {
            throw new ApiException("CareerWorld with id " + id + " not found");
        }
        return toOut(careerWorlds.get(0));
    }

    public void create(CareerWorldInDTO dto) {
        CareerWorld careerWorld = modelMapper.map(dto, CareerWorld.class);

        careerWorldRepository.save(careerWorld);
    }

    public void update(Integer id, CareerWorldInDTO dto) {
        List<CareerWorld> careerWorlds = careerWorldRepository.findCareerWorldById(id);
        if (careerWorlds.isEmpty()) {
            throw new ApiException("CareerWorld with id " + id + " not found");
        }
        CareerWorld careerWorld = careerWorlds.get(0);

        modelMapper.map(dto, careerWorld);

        careerWorldRepository.save(careerWorld);
    }

    public void delete(Integer id) {
        List<CareerWorld> careerWorlds = careerWorldRepository.findCareerWorldById(id);
        if (careerWorlds.isEmpty()) {
            throw new ApiException("CareerWorld with id " + id + " not found");
        }
        careerWorldRepository.delete(careerWorlds.get(0));
    }

    // ---------- helpers ----------

    private CareerWorldOutDTO toOut(CareerWorld careerWorld) {
        return modelMapper.map(careerWorld, CareerWorldOutDTO.class);
    }
}
