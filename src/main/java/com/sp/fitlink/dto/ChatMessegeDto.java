package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class ChatMessegeDto {
    private String Sender; //보낸사람
    private String message; //내용
    private String roomId; //방 ID( 1:1 채팅 식별용 )
}
