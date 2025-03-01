import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rats.R
import com.rats.models.Message
import com.rats.ui.viewHolders.MessageViewHolder

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message_vh, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.userName.text = message.sender.name;
        holder.messageContent.text = message.content;
        holder.messageDate.text = message.createdAt;
    }

    override fun getItemCount(): Int {
        return messages.size;
    }

}