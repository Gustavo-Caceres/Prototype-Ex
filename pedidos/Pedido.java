package pedidos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Prototype<Pedido>, Serializable {
    private static long proximoId = 1;

    private long id;
    private String cliente;
    private String enderecoEntrega;
    private List<ItemPedido> itens = new ArrayList<>();
    private LocalDate criadoEm;

    public Pedido(String cliente, String enderecoEntrega) {
        this.id = gerarId();
        this.criadoEm = LocalDate.now();
        this.cliente = cliente;
        this.enderecoEntrega = enderecoEntrega;
    }

    private static synchronized long gerarId() {
        return proximoId++;
    }

    public void adicionar(ItemPedido item) {
        itens.add(item);
    }

    public ItemPedido buscarItem(String produto) {
        for (ItemPedido item : itens) {
            if (item.getProduto().equals(produto)) {
                return item;
            }
        }
        throw new IllegalArgumentException("Produto não encontrado no pedido: " + produto);
    }

    public BigDecimal valorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            total = total.add(item.subtotal());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Pedido clonar() {
        Pedido copia = new Pedido(this.cliente, this.enderecoEntrega);
        for (ItemPedido item : this.itens) {
            copia.itens.add(new ItemPedido(item));
        }
        return copia;
    }

    public Pedido clonarPorSerializacao() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                out.writeObject(this);
            }
            Pedido copia;
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                copia = (Pedido) in.readObject();
            }
            copia.id = gerarId();
            copia.criadoEm = LocalDate.now();
            return copia;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Erro ao clonar pedido " + id, e);
        }
    }

    public long getId() { return id; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }

    public List<ItemPedido> getItens() { return itens; }

    public LocalDate getCriadoEm() { return criadoEm; }

    @Override
    public String toString() {
        return "Pedido #" + id + " | " + cliente + " | " + enderecoEntrega + " | itens=" + itens.size()
                + " | total=R$ " + valorTotal().toPlainString().replace('.', ',');
    }
}
