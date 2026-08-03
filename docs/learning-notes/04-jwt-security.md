# 04 - JWT Security

## Hoc truoc khi code

JWT dung cho stateless authentication. Client login lay token, moi request gui `Authorization: Bearer <token>`.

Gateway verify token truoc, sau do forward user info vao service noi bo.

## Cau nho phong van

JWT stateless giup server khong can luu session. Gateway co the kiem tra token tap trung truoc khi route request vao service phia sau.
