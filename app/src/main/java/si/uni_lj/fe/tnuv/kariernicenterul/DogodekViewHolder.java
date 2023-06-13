package si.uni_lj.fe.tnuv.kariernicenterul;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DogodekViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView eventName, eventLocation, eventDate, eventHost;

    public DogodekViewHolder(@NonNull View itemView) {
        super(itemView);
        eventName = itemView.findViewById(R.id.eventName);
        eventLocation = itemView.findViewById(R.id.eventLocation);
        eventDate = itemView.findViewById(R.id.eventDate);
        eventHost = itemView.findViewById(R.id.eventHost);

    }

    @Override
    public void onClick(View v) {

    }
}
