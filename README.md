# PRM392_HorseRacingProject
✅ 1. Quy tắc đặt tên ID trong layout (id của View)
Loại View	Quy tắc đặt tên	Ví dụ
Button	btn_<chức năng>	btnLogin, btnDangKy
TextView	tv_<nội dung>	tvTitle, tvThongBao
EditText	edt_<nội dung>	edtUsername, edtMatKhau
ImageView	img_<nội dung>	imgAvatar, imgLogo
ListView	lv_<nội dung>	lvDanhSach, lvTinNhan
RecyclerView	rv_<nội dung>	rvSanPham, rvBinhLuan
LinearLayout	layout_<vị trí> hoặc <nội dung>	layoutTop, layoutForm
ConstraintLayout	tương tự	layoutContainer, layoutMain

🔥 Tips: Tên nên viết bằng camelCase, rõ nghĩa, không viết tắt linh tinh khó hiểu.

✅ 2. Tên file layout (XML)
Quy tắc: snake_case, mô tả chức năng/màn hình

Ví dụ:

activity_login.xml

activity_register.xml

item_product.xml (cho 1 item trong list)

dialog_add_fruit.xml (giao diện dialog)

🔁 Đặt tên theo chức năng UI, bắt đầu bằng activity_, fragment_, item_, dialog_,...

✅ 3. Tên class Activity / Fragment
Quy tắc: PascalCase, kết thúc bằng Activity, Fragment

Ví dụ:

LoginActivity, MainActivity

RegisterActivity

HomeFragment, ProfileFragment

✅ 4. Tên biến (variable)
Loại biến	Quy tắc đặt tên	Ví dụ
Biến thường	camelCase	userName, listProduct
Biến hằng số (const)	SCREAMING_SNAKE_CASE	MAX_LOGIN_TRY, API_KEY
Biến trong XML Binding	giống ID View	btnLogin, tvWelcome

✅ 5. Tên Adapter, Model, Utils
Loại class	Tên gợi ý	Ví dụ
Model	PascalCase, dạng danh từ	User, Product, Fruit
Adapter	PascalCase, kết thúc Adapter	FruitAdapter, UserAdapter
Helper/Utils	Kết thúc Helper, Utils	StringUtils, DateHelper

✅ 6. Tên hàm (method)
Quy tắc: camelCase, bắt đầu bằng động từ

Ví dụ:

getUserInfo()

loadData()

handleLogin()

onRegisterClick()

🧠 Tổng Kết: Tư duy đặt tên = rõ ràng + logic + không dài dòng
Thành phần	Quy tắc	Ví dụ
View ID	prefix_nộiDung	btnSave, edtPassword
Layout XML	snake_case	activity_home.xml, item_user
Class	PascalCase	LoginActivity, UserAdapter
Biến	camelCase	userList, productImage
Hằng số	SCREAMING_SNAKE_CASE	DEFAULT_TIMEOUT, DB_VERSION
Method	camelCase + động từ	handleClick(), loadImage()
