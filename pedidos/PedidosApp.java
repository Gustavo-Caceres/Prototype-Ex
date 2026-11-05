package pedidos;

import java.math.BigDecimal;
import java.util.List;

public class PedidosApp {
    public static void main(String[] args) {
        Pedido modelo = new Pedido("(modelo)", "-");
        modelo.adicionar(new ItemPedido("Café 500g", 10, new BigDecimal("18.90")));
        modelo.adicionar(new ItemPedido("Açúcar 1kg", 20, new BigDecimal("4.50")));
        modelo.adicionar(new ItemPedido("Filtro de papel", 5, new BigDecimal("7.20")));

        CatalogoDePedidos catalogo = new CatalogoDePedidos();
        catalogo.registrar("reposicao-mensal", modelo);

        Pedido a = catalogo.criar("reposicao-mensal");
        a.setCliente("Mercado Central");
        a.setEnderecoEntrega("Rua das Flores, 120");

        Pedido b = catalogo.criar("reposicao-mensal");
        b.setCliente("Padaria Boa Massa");
        b.setEnderecoEntrega("Av. Paraná, 800");
        b.buscarItem("Café 500g").setQuantidade(20);

        Pedido c = catalogo.criar("reposicao-mensal");
        c.setCliente("Restaurante do Porto");
        c.setEnderecoEntrega("Rua do Cais, 45");
        c.adicionar(new ItemPedido("Adoçante 100ml", 12, new BigDecimal("6.00")));

        System.out.println(modelo);
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println();

        verificar("CA-01", "ids diferentes",
                modelo.getId() != a.getId() && modelo.getId() != b.getId() && modelo.getId() != c.getId()
                        && a.getId() != b.getId() && a.getId() != c.getId() && b.getId() != c.getId());

        verificar("CA-02", "total do modelo continua R$ 315,00",
                modelo.valorTotal().compareTo(new BigDecimal("315.00")) == 0);

        verificar("CA-03", "totais de A, B e C corretos",
                a.valorTotal().compareTo(new BigDecimal("315.00")) == 0
                        && b.valorTotal().compareTo(new BigDecimal("504.00")) == 0
                        && c.valorTotal().compareTo(new BigDecimal("387.00")) == 0);

        verificar("CA-04", "modelo continua com 3 itens", modelo.getItens().size() == 3);

        verificar("CA-05", "itens das cópias não são instâncias do modelo",
                naoCompartilhaItens(modelo, a) && naoCompartilhaItens(modelo, b) && naoCompartilhaItens(modelo, c));

        boolean lancou = false;
        try {
            catalogo.criar("inexistente");
        } catch (IllegalArgumentException e) {
            lancou = true;
        }
        verificar("CA-06", "chave inexistente lança IllegalArgumentException", lancou);

        verificar("CA-07", "cliente do modelo continua (modelo)", modelo.getCliente().equals("(modelo)"));

        System.out.println();
        int quantidade = 100_000;

        long inicio = System.nanoTime();
        for (int i = 0; i < quantidade; i++) {
            modelo.clonar();
        }
        long tempoManual = (System.nanoTime() - inicio) / 1_000_000;

        inicio = System.nanoTime();
        for (int i = 0; i < quantidade; i++) {
            modelo.clonarPorSerializacao();
        }
        long tempoSerializacao = (System.nanoTime() - inicio) / 1_000_000;

        System.out.println(quantidade + " cópias com clonar(): " + tempoManual + " ms");
        System.out.println(quantidade + " cópias com clonarPorSerializacao(): " + tempoSerializacao + " ms");
    }

    private static void verificar(String id, String descricao, boolean resultado) {
        System.out.println(id + " " + descricao + ": " + (resultado ? "OK" : "FALHA"));
    }

    private static boolean naoCompartilhaItens(Pedido modelo, Pedido copia) {
        List<ItemPedido> itensModelo = modelo.getItens();
        for (ItemPedido itemCopia : copia.getItens()) {
            for (ItemPedido itemModelo : itensModelo) {
                if (itemCopia == itemModelo) {
                    return false;
                }
            }
        }
        return true;
    }
}
