package com.campingmanager.stats.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StatsResponse {
    private OccupazioneStats occupazione;
    private Map<String, Long> noleggiPerStato;

    @Data
    public static class OccupazioneStats {
        private long totale;
        private long disponibili;
        private long occupati;
        private long inManutenzione;
        private double percentualeOccupazione;
    }
}
