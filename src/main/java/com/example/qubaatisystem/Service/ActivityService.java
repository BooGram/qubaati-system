package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.ActivityInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityOutDTO;
import com.example.qubaatisystem.Model.Activity;
import com.example.qubaatisystem.Repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ModelMapper modelMapper;

    public List<ActivityOutDTO> getAll() {
        return activityRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public ActivityOutDTO getById(Integer id) {
        List<Activity> activities = activityRepository.findActivityById(id);
        if (activities.isEmpty()) {
            throw new ApiException("Activity with id " + id + " not found");
        }
        return toOut(activities.get(0));
    }

    public void create(ActivityInDTO dto) {
        Activity activity = modelMapper.map(dto, Activity.class);

        activityRepository.save(activity);
    }

    public void update(Integer id, ActivityInDTO dto) {
        List<Activity> activities = activityRepository.findActivityById(id);
        if (activities.isEmpty()) {
            throw new ApiException("Activity with id " + id + " not found");
        }
        Activity activity = activities.get(0);

        modelMapper.map(dto, activity);

        activityRepository.save(activity);
    }

    public void delete(Integer id) {
        List<Activity> activities = activityRepository.findActivityById(id);
        if (activities.isEmpty()) {
            throw new ApiException("Activity with id " + id + " not found");
        }
        activityRepository.delete(activities.get(0));
    }

    // ---------- helpers ----------

    private ActivityOutDTO toOut(Activity activity) {
        return modelMapper.map(activity, ActivityOutDTO.class);
    }
}
