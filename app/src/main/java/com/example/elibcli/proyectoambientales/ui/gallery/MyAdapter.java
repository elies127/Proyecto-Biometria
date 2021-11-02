package com.example.elibcli.proyectoambientales.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elibcli.proyectoambientales.R;
import com.example.elibcli.proyectoambientales.data.model.FirebaseLogicaNegocio;
import com.example.elibcli.proyectoambientales.data.model.LoggedInUser;
import com.example.elibcli.proyectoambientales.data.model.Nodo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;

    ArrayList<Nodo> list;


    public MyAdapter(Context context, ArrayList<Nodo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Nodo node = list.get(position);
        holder.beaconName.setText(node.getBeaconName());
        holder.uuid.setText(node.getUuid());

        holder.noise.setText(String.valueOf(node.calcularDistancia(node.getNoise(), node.getTxPower())));
        holder.major.setText(String.valueOf(node.getMajor()));
        holder.minor.setText(String.valueOf(node.getMinor()));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                FirebaseLogicaNegocio logica = new FirebaseLogicaNegocio();
                FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser(); //Obtenemos los datos del usuario que ha iniciado sesion
                if(usuario != null) { //vemos si el usuario ha iniciado sesión..


                    LoggedInUser usuarioLogged = new LoggedInUser(usuario.getUid(), usuario.getDisplayName(), usuario.getEmail()); //transformamos los datos de FirebaseAuth a los nuestros

                    logica.desenlazarDispositivos(node, usuarioLogged);
                    list.remove(position);  // remove the item from list
                    notifyItemRemoved(position); // notify the adapter about the removed item
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView beaconName, uuid, noise, major, minor;
        Button delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            beaconName = itemView.findViewById(R.id.recycler_beaconName);
            uuid = itemView.findViewById(R.id.recycler_uuid);
            noise = itemView.findViewById(R.id.recycler_noise);
            delete = itemView.findViewById(R.id.btnDeleteBeacon);
            major = itemView.findViewById(R.id.recycler_major); //De momento no mostraremos major o minor
            minor = itemView.findViewById(R.id.recycler_minor);

        }
    }

}
