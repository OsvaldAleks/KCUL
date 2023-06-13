package si.uni_lj.fe.tnuv.kariernicenterul;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class DogodekAdapter extends RecyclerView.Adapter<DogodekAdapter.MyViewHolder> {

    private final RecyclerClickListener recyclerClickListener;
    Context context;
    ArrayList<Dogodek> seznam;

    public DogodekAdapter(Context context, ArrayList<Dogodek> seznam, RecyclerClickListener recyclerClickListener) {
        this.recyclerClickListener = recyclerClickListener;
        this.context = context;
        this.seznam = seznam;
    }

    @NonNull
    @Override
    public DogodekAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.event_info, parent, false);
        return new MyViewHolder(v, recyclerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DogodekAdapter.MyViewHolder holder, int position) {
        Dogodek dogodek = seznam.get(position);
        holder.eventName.setText(dogodek.getIme());
        holder.eventDate.setText(dogodek.getDatum());
        holder.eventLocation.setText(dogodek.getLokacija());
        holder.eventHost.setText(dogodek.getPredavatelj());

    }

    @Override
    public int getItemCount() {
        return seznam.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventLocation, eventDate, eventHost;

        public MyViewHolder(@NonNull View itemView, RecyclerClickListener recyclerClickListener) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventHost = itemView.findViewById(R.id.eventHost);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerClickListener != null){
                        int pos = getAbsoluteAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerClickListener.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
