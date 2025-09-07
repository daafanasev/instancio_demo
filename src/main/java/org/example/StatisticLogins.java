package org.example;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatisticLogins {

    private LocalDateTime dateTime;

    private Integer durationSession;

}
