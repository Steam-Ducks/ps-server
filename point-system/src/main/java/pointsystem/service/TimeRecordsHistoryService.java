package pointsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pointsystem.converter.TimeRecordsHistoryConverter;
import pointsystem.dto.timeRecordsHistory.TimeRecordsHistoryDto;
import pointsystem.entity.TimeRecordsHistory;
import pointsystem.repository.TimeRecordsHistoryRepository;

import java.util.List;

@Service
public class TimeRecordsHistoryService {

    @Autowired
    private TimeRecordsHistoryRepository historyRepository;

    @Autowired
    private TimeRecordsHistoryConverter historyConverter;

    public List<TimeRecordsHistoryDto> getHistoryByTimeRecordsId(int timeRecordsId) {
        List<TimeRecordsHistory> historyList = historyRepository.findHistoryByTimeRecordsId(timeRecordsId);
        return historyConverter.toDto(historyList);
    }
}

