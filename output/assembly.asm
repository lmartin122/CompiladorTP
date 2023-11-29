.386
.model flat, stdcall
option casemap :none
include \masm32\include\windows.inc
include \masm32\include\kernel32.inc
include \masm32\include\masm32.inc
includelib \masm32\lib\kernel32.lib
include \masm32\include\user32.inc
includelib \masm32\lib\masm32.lib
includelib \masm32\lib\user32.lib
.DATA
@variable2bytes dw ? 
_OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO db "Error: se excedió el límite permitido (overflow)", 0
_OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO db "Error: se excedió el límite permitido (overflow)", 0
_OVERFLOW_SUMA_PFLOTANTE db "Error: se excedió el límite permitido (overflow)", 0
_INVOCACION_RECURSIVA db "Error: no se permiten declaraciones recursivas.", 0
_ERROR_POR_PANTALLA db "Error: se terminará el programa.", 0
_RECURSIVIDAD db "Error: no se permiten llamadas recursivas.", 0
_flagRecursividad DWORD 0
_cte_17976931348623155E308 dq 1.7976931348623155E308
@aux2@main dq ? 
__a@main dq ? 
__b@main dq ? 
__c@main dq ? 
.CODE
recursividad:
invoke MessageBoxA, NULL, ADDR _RECURSIVIDAD, ADDR _RECURSIVIDAD, MB_OK 
invoke ExitProcess, 0

overflow_UINT:
invoke MessageBoxA, NULL, ADDR _OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO, ADDR _OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO, MB_OK 
invoke ExitProcess, 0

overflow_LONG:
invoke MessageBoxA, NULL, ADDR _OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO, ADDR _OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO, MB_OK 
invoke ExitProcess, 0

overflow_DOUBLE:
invoke MessageBoxA, NULL, ADDR _OVERFLOW_SUMA_PFLOTANTE, ADDR _OVERFLOW_SUMA_PFLOTANTE, MB_OK 
invoke ExitProcess, 0

@main:
FLD _cte_17976931348623155E308
FST __a@main
FLD _cte_17976931348623155E308
FST __b@main
FLD __b@main
FADD __a@main
FST @aux2@main
JC overflow_DOUBLE
FLD @aux2@main
FST __c@main
invoke ExitProcess, 0
end @main

