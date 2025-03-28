package com.example.mytest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.RoomCodeActivity;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Room;
import com.example.mytest.repository.RoomRepository;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RoomHistoryAdapter extends RecyclerView.Adapter<RoomHistoryAdapter.RoomViewHolder> {

    private final List<Room> roomList;
    private final Context context;

    public RoomHistoryAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_history, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        // Установка данных комнаты
        holder.tvRoomCode.setText("Код комнаты: " + room.getRoomNumber());
        holder.tvTestName.setText("Название теста: " + (room.getTestName() != null ? room.getTestName() : "Без названия"));

        // Открытие комнаты по клику
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomCodeActivity.class);
            TestRepository testRepository = new TestRepository(FirebaseFirestore.getInstance());
            testRepository.getById(room.getTestId()).thenAccept(test -> {
                intent.putExtra("ROOM_CODE", room.getRoomNumber());
                intent.putExtra("TEST_NAME", room.getTestName());
                intent.putExtra("ROOM_ID", room.getId());
                Select.setTest(test);
                context.startActivity(intent);
            });
        });

        // Обработка удаления комнаты при нажатии на иконку мусорки
        holder.deleteIcon.setOnClickListener(v -> {
            RoomRepository roomRepository = new RoomRepository(FirebaseFirestore.getInstance());

            roomRepository.deleteRoom(room); // Удаление из Firestore
            roomList.remove(position); // Удаление из списка
            notifyItemRemoved(position); // Обновление списка
            notifyItemRangeChanged(position, roomList.size()); // Перерисовка списка
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {

        TextView tvRoomCode;
        TextView tvTestName;
        ImageView deleteIcon;


        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomCode = itemView.findViewById(R.id.tvRoomCode);
            tvTestName = itemView.findViewById(R.id.tvTestName);
            deleteIcon = itemView.findViewById(R.id.notificationIcon);
        }
    }
}
