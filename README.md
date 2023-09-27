# Proyecto de Compilador Educativo
Este es un proyecto de compilador que demuestra la construcción de un compilador simple capaz de realizar análisis léxico, sintáctico, semántico, generar código intermedio y generacion de errores sin interrupcion durante el compilado. Este compilador se ha desarrollado como una herramienta educativa para comprender los conceptos fundamentales detrás de la compilación de programas.


## Introducción
Este proyecto de compilador demuestra la creación de un compilador simple y educativo que abarca no solo el análisis léxico, sintáctico y semántico, ademas de la generación de código intermedio y la gestión de errores sin interrupciones durante el proceso de compilación. Ha sido desarrollado con la finalidad de facilitar la comprensión de los conceptos fundamentales involucrados en la compilación de programas.

## Estructura del Proyecto

````bash
CompiladorTP/
│
├── Lexico/           # Módulo de análisis léxico
├── Sintactico/       # Módulo de análisis sintáctico
├── CIntermedio/      # Módulo de generación de código intermedio
├── Tools/            # Módulo de herramientas utilizadas
├── data/             # Datos para cargar la matriz de transicion y la tabla de palabras reservadas
├── sample_programs/  # Directorio con ejemplos de programas fuente
│   ├── program1.txt
│   ├── program2.txt
│   └── ...
│
└── README.md         # Este archivo README
````

## Cómo Usar

1. Clona este repositorio en tu máquina local:

````bash
git clone https://github.com/lmartin122/CompiladorTP.git
````

2. Navega al directorio del proyecto:
````bash
cd CompiladorTP
````

3. Ejecuta el compilador en un programa fuente de ejemplo:
````bash
java /src/Sintactico/Parser.java
````

4. El compilador realizará el análisis léxico, sintáctico, semántico y generará código intermedio para el programa fuente proporcionado. Además, gestionará los errores de manera que el proceso de compilación no se interrumpa, permitiéndote identificar y corregir múltiples problemas en un solo intento.

5. Explora los archivos generados y los mensajes de error para comprender el proceso de compilación y mejorar tus habilidades en la detección y corrección de errores.

## Licencia
Este proyecto se distribuye bajo la Licencia UNICEN.
