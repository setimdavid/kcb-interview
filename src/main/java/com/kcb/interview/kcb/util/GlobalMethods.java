package com.kcb.interview.kcb.util;

import com.kcb.interview.kcb.model.ClientAuth;
import com.kcb.interview.kcb.repository.ClientAuthRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

@Log
@Component
@SuppressWarnings("Duplicates")
public class GlobalMethods {

    private final ClientAuthRepository userLoginRepository;

    @Autowired
    public GlobalMethods(ClientAuthRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    public ClientAuth fetchUserDetails() {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String clientKey = client.getUsername();
        ClientAuth clientDetails = userLoginRepository.findClientAuthByClientKey(clientKey);
        return clientDetails;
    }

    public Date getDateFormatCorrect(String date, Integer type) {
        Date mydate = null;
        try {
            mydate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        } catch (java.text.ParseException e) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            mydate = today.getTime();
            log.log(Level.WARNING, "Date format is not correct");
            return mydate;
        }
        Calendar calendarOneTwo = Calendar.getInstance();
        calendarOneTwo.setTime(mydate);
        calendarOneTwo.add(Calendar.DATE, type);
        mydate = calendarOneTwo.getTime();
        return mydate;
    }

    public String timeFormatter(Timestamp time) {
        LocalDateTime myDateObj = time.toLocalDateTime();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);
        return formattedDate;
    }

    public Integer[] convertListToIntegerArray(List<Integer> list) {
        Integer[] arr = new Integer[list.size()];
        for (int i = 0; i < list.size(); i++)
            arr[i] = list.get(i);
        return arr;
    }

    public String getFormattedDate(Timestamp timestamp) {
        String formatTime = Timestamp.from(Instant.now()).toString();
        try {
            String pattern = "dd-MM-yyyy HH:mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date date = new Date(timestamp.getTime());
            formatTime = simpleDateFormat.format(date);
        } catch (Exception ex) {
        }
        return formatTime;
    }
}

