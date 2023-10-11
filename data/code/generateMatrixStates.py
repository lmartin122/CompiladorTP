import pandas as pd


sheet_id = {"e":"1rLdF8W5zbBylfM9_4pAV8vZYeowHRthVz-fgfouAEHE", 
            "as":"1LNSfD3MincJ71Ffr1QjJxSHvT3z4Km4R4hJusOmg97M"}

change = {"F": 20,
          "E" : -1,
          "nan": "chequear", 
          "A0": "null",
          "A1": "ASAnadirBuffer",
          "A2": "ASLiterales",
          "A3": "ASDevolverIdentificador",
          "A4": "ASRangoEntero",
          "A5": "ASRangoEnteroLargo",
          "A6": "ASRangoDouble",
          "A7": "ASChequeoOperador",
          "A8": "ASChequeoOperador",
          "A9": "null",
          "A10": "ASCadena",
          "A11": "ASLimpiarBuffer",
          "EE":"ASIntegerError",
          "EF":"ASFloatError",
          "ES":"ASSimbolError"
          }
        
df_e = pd.read_csv(f"https://docs.google.com/spreadsheets/d/{sheet_id['e']}/export?format=csv")
df_e = df_e.drop(df_e.columns[0], axis=1)

df_as = pd.read_csv(f"https://docs.google.com/spreadsheets/d/{sheet_id['as']}/export?format=csv")
df_as = df_as.drop(df_as.columns[0], axis=1)


with open('../matrizTransicion.txt', 'w') as output_file:
    for (ind_e, row_e), (ind_as, row_as) in zip(df_e.iterrows(), df_as.iterrows()):
        j = 0

        for col_e, col_as in zip(row_e, row_as):
            # Convierte el nombre de la columna (col_name) en el número de columna
            # Asumo que el nombre de la columna es el número de columna + 1

            if(col_e in change.keys()): col_e = change[col_e]

            # Guarda la fila, columna y valor en el archivo de salida
            output_line = f"{ind_e};{j};{col_e};{change[col_as]}\n"
            output_file.write(output_line)
            j += 1
