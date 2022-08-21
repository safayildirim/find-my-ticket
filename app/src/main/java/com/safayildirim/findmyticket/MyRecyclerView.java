package com.safayildirim.findmyticket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.safayildirim.findmyticket.models.AddWatchingRequest;
import com.safayildirim.findmyticket.models.Session;
import com.safayildirim.findmyticket.services.Client;

import java.util.List;
import java.util.Locale;

public class MyRecyclerView extends RecyclerView.Adapter<MyRecyclerView.ViewHolder> {
    private List<Session> sessions;
    private final Client client;
    private String departure;
    private String arrival;
    private String date;

    public MyRecyclerView(List<Session> sessions, String departure, String arrival, String date) {
        this.sessions = sessions;
        this.date = date;
        this.departure = departure;
        this.arrival = arrival;
        client = new Client();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView departure;
        private final TextView arrival;
        private final TextView duration;
        private final TextView availableSeats;
        private final ImageView alarmView;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            departure = view.findViewById(R.id.departure_time);
            arrival = view.findViewById(R.id.arrival_time);
            duration = view.findViewById(R.id.duration);
            availableSeats = view.findViewById(R.id.available_seats);
            alarmView = view.findViewById(R.id.alarm_imageView);

        }

        public TextView getDeparture() {
            return departure;
        }

        public TextView getArrival() {
            return arrival;
        }

        public TextView getDuration() {
            return duration;
        }

        public TextView getAvailableSeats() {
            return availableSeats;
        }

        public ImageView getAlarmView() {
            return alarmView;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_session_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Session currentSession = sessions.get(position);
        viewHolder.getDeparture().setText(currentSession.getDeparture());
        viewHolder.getArrival().setText(currentSession.getArrival());
        viewHolder.getDuration().setText(currentSession.getDuration());
        viewHolder.getAvailableSeats().setText(String.format(Locale.getDefault(), "%d", currentSession.getAvailableSeats()));
        if (currentSession.isWatching()) {
            viewHolder.getAlarmView().setImageResource(R.drawable.opened_alarm);
        } else {
            viewHolder.getAlarmView().setImageResource(R.drawable.normal_alarm);
        }
        viewHolder.getAlarmView().setOnClickListener(view -> {
            AddWatchingRequest request = new AddWatchingRequest(departure, arrival, date, currentSession.getDeparture());
            if (currentSession.isWatching()) {
                viewHolder.getAlarmView().setImageResource(R.drawable.normal_alarm);
                currentSession.setWatching(false);
                client.setWatching(request, AddWatchingRequest.CommandType.REMOVE, new OnCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
            } else {
                viewHolder.getAlarmView().setImageResource(R.drawable.opened_alarm);
                currentSession.setWatching(true);
                client.setWatching(request, AddWatchingRequest.CommandType.ADD, new OnCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
