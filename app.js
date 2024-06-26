const express = require('express');
const { Pool } = require('pg');

const app = express();

const PORT = 3000;

const DB = new Pool({
    user: 'postgres',
    host: '192.168.1.71',
    database: 'tienda',
    password: '3008',
    port: 5432
});

app.get('/productos', async (request, response) => {

    try{

        const { rows } = await DB.query('SELECT * FROM productos');

        return response.status(200).json(rows).end();

    }catch(err) {
        return response.status(500).json({
            mensaje: 'A ocurrido un error en el servidor'
        }).end();
    }

});

app.get('/productos/:id', async (request, response) => {
    const { id } = request.params;

    try {
        const {rows} = await DB.query('SELECT * FROM productos WHERE id = $1', [id]);

        if(rows.length === 0) {
            return response.status(404).json({
                mensaje: 'Producto no encontrado'
            }).end();
        }

        response.status(200).json(rows).end();

    } catch (err) {

        response.status(500).json({
            mensaje: 'A ocurrido un error en el servidor'
        }).end();

    }


});

app.get('/comprar/:id/:cantidad', async (request, response) => {
    const { id, cantidad } = request.params;

    try {

        // Inicio de la transaccion
        await DB.query('BEGIN');

        const { rows } = await DB.query('SELECT * FROM productos WHERE id = $1', [id]);

        if (rows.length == 0) {
            await DB.query('ROLLBACK');
            return response.status(404).json({
                mensaje: "No existe el producto seleccionado"
            }).end();
        }

        const producto = rows[0];

        if (producto.stock === 0 || producto.stock < cantidad) {
            await DB.query('ROLLBACK');
            return response.status(400).json({
                mensaje: 'No hay suficiente stock de productos'
            }).end();
        }

        await DB.query('UPDATE productos SET stock = stock - $1 WHERE id = $2', [cantidad, id]);

        await DB.query('COMMIT');

        return response.status(202).json({
            mensaje: 'Compra realizada con exito!!'
        }).end();

    } catch (err) {
        await DB.query('ROLLBACK');
        return response.status(500).json({
            mensaje: 'Algo a salido mal durante la compra'
        }).end();
    }


});



app.listen(PORT, async () => {
    try {
        await DB.connect();
        console.log('Conexion con base de datos establecida!!');
    } catch (err) {
        console.log(err);
        process.exit(0);
    }
})
