package com.stark.chatsignalr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var hubConnection: HubConnection
    private var conversa: MutableList<String> = arrayListOf("TESTE")
    private lateinit var recycle: RecyclerView
    private  lateinit var  adapter : AdapterLista
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = AdapterLista(conversa)
        recycle = findViewById<RecyclerView>(R.id.recycle)
        recycle.adapter = adapter
        recycle.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)


        hubConnection = HubConnectionBuilder.create("http://192.168.1.44:53840/chatHub")
            .withHeader("ClienteID", "1")
            .build()

//        await Clients.Others.SendAsync("ReceiveMensagem",mensagem);
        hubConnection.on("ReceiveMensagem", ({
            conversa.add(it.toString())
            runOnUiThread {
                adapter.notifyDataSetChanged()
            }

        }), String::class.java)




        if (hubConnection.connectionState != HubConnectionState.CONNECTED) {
            hubConnection.start()
        }

        findViewById<Button>(R.id.btn_enviar).setOnClickListener {
            hubConnection.send(
                "sendMensagem",
                findViewById<EditText>(R.id.editText).text.toString()
            )
        }



    }

    inner class AdapterLista(val mensagem: MutableList<String>) :
        RecyclerView.Adapter<AdapterLista.MyViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(applicationContext).inflate(
                    R.layout.list_item,
                    null
                )
            )
        }

        override fun getItemCount(): Int = mensagem.count()

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val men = mensagem[position]
            holder.text.text = men
        }

        inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val text = v.findViewById<TextView>(R.id.texto_servidor)
        }
    }
}
