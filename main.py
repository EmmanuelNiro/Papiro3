# main.py
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional
from datetime import date, datetime
import mysql.connector
from mysql.connector import Error

app = FastAPI(title="API Biblioteca", description="API CRUD para gestión de biblioteca")

# Configurar CORS para permitir peticiones desde el frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Configuración de conexión a MySQL
def obtener_conexion():
    try:
        conexion = mysql.connector.connect(
            host='localhost',
            database='papirodb',  # Cambia al nombre de tu BD
            user='root',
            password=''  # Tu contraseña de MySQL
        )
        return conexion
    except Error as e:
        print(f"Error al conectar a MySQL: {e}")
        return None

# ============== MODELOS PYDANTIC ==============

class Usuario(BaseModel):
    nombre: str
    apellidos: str
    nombre_usuario: str
    correo: Optional[str] = None
    contraseña: str
    fecha_nacimiento: date
    fecha_registro: Optional[date] = None

class UsuarioActualizar(BaseModel):
    nombre: Optional[str] = None
    apellidos: Optional[str] = None
    nombre_usuario: Optional[str] = None
    contraseña: Optional[str] = None
    fecha_nacimiento: Optional[date] = None

class LoginRequest(BaseModel):
    nombre_usuario: str
    contraseña: str

class Comentario(BaseModel):
    id_usuario: int
    id_libro: int
    comentario: str
    fecha_comentario: Optional[date] = None

class Calificacion(BaseModel):
    id_usuario: int
    libro: int
    estrellas: int
    fecha_calificacion: Optional[date] = None

class Favorito(BaseModel):
    id_usuario: int
    libro: int
    tipo_libro: str
    fecha_agregado: Optional[date] = None

class LeerMasTarde(BaseModel):
    id_usuario: int
    id_libro: int
    tipo_libro: str
    fecha_agregado: Optional[date] = None

# ============== ENDPOINTS USUARIOS ==============

@app.get("/")
def inicio():
    return {"mensaje": "API Biblioteca - Endpoints disponibles en /docs"}

@app.post("/usuarios/", status_code=201)
def crear_usuario(usuario: Usuario):
    """Crear un nuevo usuario"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión a la base de datos")

    try:
        cursor = conexion.cursor()
        query = """INSERT INTO Usuarios (nombre, apellidos, nombre_usuario, contraseña,
                   fecha_nacimiento, fecha_registro)
                   VALUES (%s, %s, %s, %s, %s, %s)"""
        fecha_reg = usuario.fecha_registro or date.today()
        valores = (usuario.nombre, usuario.apellidos, usuario.nombre_usuario,
                  usuario.contraseña, usuario.fecha_nacimiento, fecha_reg)

        cursor.execute(query, valores)
        conexion.commit()

        return {"mensaje": "Usuario creado exitosamente", "id": cursor.lastrowid}
    except Error as e:
        raise HTTPException(status_code=400, detail=f"Error al crear usuario: {str(e)}")
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.get("/usuarios/")
def obtener_usuarios():
    """Obtener todos los usuarios"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor(dictionary=True)
        cursor.execute("SELECT * FROM Usuarios")
        usuarios = cursor.fetchall()
        return {"usuarios": usuarios, "total": len(usuarios)}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.get("/usuarios/{id_usuario}")
def obtener_usuario(id_usuario: int):
    """Obtener un usuario por ID"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor(dictionary=True)
        cursor.execute("SELECT * FROM Usuarios WHERE id_usuarios = %s", (id_usuario,))
        usuario = cursor.fetchone()

        if not usuario:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")
        return usuario
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.put("/usuarios/{id_usuario}")
def actualizar_usuario(id_usuario: int, usuario: UsuarioActualizar):
    """Actualizar un usuario existente"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()

        # Construir query dinámicamente solo con campos proporcionados
        campos_actualizar = []
        valores = []

        if usuario.nombre:
            campos_actualizar.append("nombre = %s")
            valores.append(usuario.nombre)
        if usuario.apellidos:
            campos_actualizar.append("apellidos = %s")
            valores.append(usuario.apellidos)
        if usuario.nombre_usuario:
            campos_actualizar.append("nombre_usuario = %s")
            valores.append(usuario.nombre_usuario)
        if usuario.contraseña:
            campos_actualizar.append("contraseña = %s")
            valores.append(usuario.contraseña)
        if usuario.fecha_nacimiento:
            campos_actualizar.append("fecha_nacimiento = %s")
            valores.append(usuario.fecha_nacimiento)

        if not campos_actualizar:
            raise HTTPException(status_code=400, detail="No hay campos para actualizar")

        valores.append(id_usuario)
        query = f"UPDATE Usuarios SET {', '.join(campos_actualizar)} WHERE id_usuarios = %s"

        cursor.execute(query, valores)
        conexion.commit()

        if cursor.rowcount == 0:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")

        return {"mensaje": "Usuario actualizado exitosamente"}
    except Error as e:
        raise HTTPException(status_code=400, detail=f"Error al actualizar: {str(e)}")
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.delete("/usuarios/{id_usuario}")
def eliminar_usuario(id_usuario: int):
    """Eliminar un usuario"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        cursor.execute("DELETE FROM Usuarios WHERE id_usuarios = %s", (id_usuario,))
        conexion.commit()

        if cursor.rowcount == 0:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")

        return {"mensaje": "Usuario eliminado exitosamente"}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()


 # ============== ENDPOINT LOGIN ==============

@app.post("/login/")
def login(request: LoginRequest):
    """Endpoint de login para validar usuario y contraseña"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión a la base de datos")

    try:
        cursor = conexion.cursor(dictionary=True)

        # Buscar por nombre_usuario o correo
        query = """SELECT * FROM Usuarios
                   WHERE (nombre_usuario = %s OR correo = %s)
                   AND contraseña = %s"""
        cursor.execute(query, (request.nombre_usuario, request.nombre_usuario, request.contraseña))
        usuario = cursor.fetchone()

        if not usuario:
            raise HTTPException(status_code=401, detail="Usuario o contraseña incorrectos")

        # Retornar datos del usuario (sin la contraseña por seguridad)
        usuario.pop('contraseña', None)

        return usuario

    except Error as e:
        raise HTTPException(status_code=400, detail=f"Error en la consulta: {str(e)}")
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()


# ============== ENDPOINTS COMENTARIOS ==============

@app.post("/comentarios/", status_code=201)
def crear_comentario(comentario: Comentario):
    """Crear un nuevo comentario"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        query = """INSERT INTO Comentarios (id_usuario, id_libro, comentario, fecha_comentario)
                   VALUES (%s, %s, %s, %s)"""
        fecha = comentario.fecha_comentario or date.today()
        valores = (comentario.id_usuario, comentario.id_libro, comentario.comentario, fecha)

        cursor.execute(query, valores)
        conexion.commit()

        return {"mensaje": "Comentario creado exitosamente", "id": cursor.lastrowid}
    except Error as e:
        raise HTTPException(status_code=400, detail=f"Error: {str(e)}")
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.get("/comentarios/")
def obtener_comentarios():
    """Obtener todos los comentarios"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor(dictionary=True)
        cursor.execute("SELECT * FROM Comentarios")
        comentarios = cursor.fetchall()
        return {"comentarios": comentarios, "total": len(comentarios)}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.get("/comentarios/{id_comentario}")
def obtener_comentario(id_comentario: int):
    """Obtener un comentario por ID"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor(dictionary=True)
        cursor.execute("SELECT * FROM Comentarios WHERE id_comentario = %s", (id_comentario,))
        comentario = cursor.fetchone()

        if not comentario:
            raise HTTPException(status_code=404, detail="Comentario no encontrado")
        return comentario
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.delete("/comentarios/{id_comentario}")
def eliminar_comentario(id_comentario: int):
    """Eliminar un comentario"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        cursor.execute("DELETE FROM Comentarios WHERE id_comentario = %s", (id_comentario,))
        conexion.commit()

        if cursor.rowcount == 0:
            raise HTTPException(status_code=404, detail="Comentario no encontrado")

        return {"mensaje": "Comentario eliminado exitosamente"}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

# ============== ENDPOINTS CALIFICACIONES ==============

@app.post("/calificaciones/", status_code=201)
def crear_calificacion(calificacion: Calificacion):
    """Crear una nueva calificación"""
    if calificacion.estrellas < 1 or calificacion.estrellas > 5:
        raise HTTPException(status_code=400, detail="Las estrellas deben estar entre 1 y 5")

    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        query = """INSERT INTO Calificaciones (id_usuario, libro, estrellas, fecha_calificacion) 
                   VALUES (%s, %s, %s, %s)"""
        fecha = calificacion.fecha_calificacion or date.today()
        valores = (calificacion.id_usuario, calificacion.libro, calificacion.estrellas, fecha)

        cursor.execute(query, valores)
        conexion.commit()

        return {"mensaje": "Calificación creada exitosamente", "id": cursor.lastrowid}
    except Error as e:
        raise HTTPException(status_code=400, detail=f"Error: {str(e)}")
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.get("/calificaciones/")
def obtener_calificaciones():
    """Obtener todas las calificaciones"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor(dictionary=True)
        cursor.execute("SELECT * FROM Calificaciones")
        calificaciones = cursor.fetchall()
        return {"calificaciones": calificaciones, "total": len(calificaciones)}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.delete("/calificaciones/{id_calificacion}")
def eliminar_calificacion(id_calificacion: int):
    """Eliminar una calificación"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        cursor.execute("DELETE FROM Calificaciones WHERE id_calificacion = %s", (id_calificacion,))
        conexion.commit()

        if cursor.rowcount == 0:
            raise HTTPException(status_code=404, detail="Calificación no encontrada")

        return {"mensaje": "Calificación eliminada exitosamente"}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

# ============== ENDPOINTS FAVORITOS ==============

@app.post("/favoritos/", status_code=201)
def crear_favorito(favorito: Favorito):
    """Agregar un libro a favoritos"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        query = """INSERT INTO Favoritos (id_usuario, libro, tipo_libro, fecha_agregado) 
                   VALUES (%s, %s, %s, %s)"""
        fecha = favorito.fecha_agregado or date.today()
        valores = (favorito.id_usuario, favorito.libro, favorito.tipo_libro, fecha)

        cursor.execute(query, valores)
        conexion.commit()

        return {"mensaje": "Favorito agregado exitosamente", "id": cursor.lastrowid}
    except Error as e:
        raise HTTPException(status_code=400, detail=f"Error: {str(e)}")
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.get("/favoritos/usuario/{id_usuario}")
def obtener_favoritos_usuario(id_usuario: int):
    """Obtener favoritos de un usuario"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor(dictionary=True)
        cursor.execute("SELECT * FROM Favoritos WHERE id_usuario = %s", (id_usuario,))
        favoritos = cursor.fetchall()
        return {"favoritos": favoritos, "total": len(favoritos)}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.delete("/favoritos/{id_favorito}")
def eliminar_favorito(id_favorito: int):
    """Eliminar un favorito"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        cursor.execute("DELETE FROM Favoritos WHERE id_favorito = %s", (id_favorito,))
        conexion.commit()

        if cursor.rowcount == 0:
            raise HTTPException(status_code=404, detail="Favorito no encontrado")

        return {"mensaje": "Favorito eliminado exitosamente"}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

# ============== ENDPOINTS LEER MÁS TARDE ==============

@app.post("/leer-mas-tarde/", status_code=201)
def crear_leer_mas_tarde(item: LeerMasTarde):
    """Agregar un libro a leer más tarde"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        query = """INSERT INTO leer_mas_tarde (id_usuario, id_libro, tipo_libro, fecha_agregado)
                   VALUES (%s, %s, %s, %s)"""
        fecha = item.fecha_agregado or date.today()
        valores = (item.id_usuario, item.id_libro, item.tipo_libro, fecha)

        cursor.execute(query, valores)
        conexion.commit()

        return {"mensaje": "Libro agregado a 'Leer más tarde'", "id": cursor.lastrowid}
    except Error as e:
        raise HTTPException(status_code=400, detail=f"Error: {str(e)}")
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.get("/leer-mas-tarde/usuario/{id_usuario}")
def obtener_leer_mas_tarde_usuario(id_usuario: int):
    """Obtener lista 'leer más tarde' de un usuario"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor(dictionary=True)
        cursor.execute("SELECT * FROM leer_mas_tarde WHERE id_usuario = %s", (id_usuario,))
        items = cursor.fetchall()
        return {"leer_mas_tarde": items, "total": len(items)}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()

@app.delete("/leer-mas-tarde/{id_leer}")
def eliminar_leer_mas_tarde(id_leer: int):
    """Eliminar un libro de 'leer más tarde'"""
    conexion = obtener_conexion()
    if not conexion:
        raise HTTPException(status_code=500, detail="Error de conexión")

    try:
        cursor = conexion.cursor()
        cursor.execute("DELETE FROM leer_mas_tarde WHERE id_leer = %s", (id_leer,))
        conexion.commit()

        if cursor.rowcount == 0:
            raise HTTPException(status_code=404, detail="Registro no encontrado")

        return {"mensaje": "Libro eliminado de 'Leer más tarde'"}
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()




#usar uvicorn main:app --reload --host  *ip de la compu* --port 8000
#para activar la aplicacion
#la parte de la ip se cambia a la que te da al conectarte a la red (Usar la asignada, no la ip gateway)
