pragma solidity  >=0.6.0 <=0.7.4;


import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
contract  ProductRegister is ERC20{
    
    address public my_addr;//내 주소 
    product_info[] public product_list;//품목 배열 
    transaction_info[] public transaction_list;//거래 내역 

    mapping(address=>uint256) balances;//계좌와 계좌잔고를 매핑함 
    mapping(address=>uint)my_index;//내 거래횟수
    //structure
    struct product_info{
        address  owner;//소유자 주소 
        string  name;//소유자 이름
        uint price;//물품 가격
        string product_url;
        //판매 종료 여부
        bool active;//active가 false이면 판매가 된 상품, true이면 판매중인 상품
        uint product_n;//물품 등록 번호
        //활성화 여부
        string product_name;//물품이름
        string description;//물품 설명
    }

    struct transaction_info{
        address buyer;//구매자 주소(내주소)
        address seller;//판매자 주소
        uint price;//물품 가격
        uint tranjection_n;//거래 등록 번호
        uint my_coin;//내 잔고
    }
    //variable
    //minter는 어드레스 타입으로 선언된 상태 address타입은 이더리움에서 사용하는 160-bit주소
    uint256 public INITIAL_SUPPLY = 12000;
    constructor() public ERC20("Tutorial", "TT") {
        _mint(msg.sender, INITIAL_SUPPLY);
    }

    //event
    //from 보내는사람, to 받는 사람, amount 양
    event Sent(address from,address to, uint price);
    event AddList(string name,uint amount, string product_url);
    event ProductCreate(address _owner,uint _productId);
    event TransactionCreate(address _owner,uint _index);
    //mapping
    //balances는 매핑타입으로 address를 key값으로 uint타입을 value로 가지는 매핑 
    


    //modifier =>제어자역할
//function createAuction(address _repoAddress, uint256 _tokenId, string memory _auctionTitle, uint256 _price) public contractIsNFTOwner(_repoAddress, _tokenId) returns(bool) {
		// 새 auction을 생성하는 함수
    function addProduct(string memory _name, uint256 _price,string memory _pname,string memory _description,string memory _url) public  {
        uint productId=product_list.length;
        product_info memory newInfo;
        newInfo.name=_name;
        newInfo.owner=msg.sender;
        newInfo.price=_price;
        newInfo.active=true;
        newInfo.product_url=_url;
        newInfo.product_n=productId;
        newInfo.product_name=_pname;
        newInfo.description=_description;
        product_list.push(newInfo);//모델 정보 리스트로 구성해서 추가
        require(product_list.length!=0);//리스트가 없으면 더이상 진행안함

        emit ProductCreate(msg.sender,productId);
        
    }
    
    function viewProductList() public{
         uint list_size=product_list.length;//전체 등록된 물품수량

        require(product_list.length!=0);//리스트가 없으면 더이상 진행안함

         for(uint i=0;i<list_size;i++)
         {  
             if(product_list[i].active==true){
                 getProductList(product_list[i].product_n);
             }
         }
    }

     function getCount() public view returns(uint) {
        return product_list.length;
	}

    function getProductList(uint _product_n) public view returns (
        //uint list_size=product_list.length;//전체 등록된 물품수량

        string memory name,
        uint price,
        uint product_id,//이건 상품 등록번호 
        address r_address,
        string memory r_photo_url,
        bool r_active,
        string memory r_description,
        string memory r_pname
        ){
        product_info memory info = product_list[_product_n];

        return (
            info.name,
            info.price,
            info.product_n,
            info.owner,
            info.product_url,
            info.active,
            info.description,
            info.product_name
        );
    }

    function checkBalance(address my_add) public view returns (uint256){//현재 나의 잔고를 리턴하는 함수
        return balances[my_add];
    }

    function addtransactionList(address my_add,address sell_add, uint sell_price) public{
        

        require(balances[my_add]>=sell_price);//물건을 살 수 있을 때
        require(my_add==msg.sender || sell_add ==msg.sender);
        uint _index=my_index[my_add]++;//내가 진행한 트랜잭션 증가
        balances[my_add]-=sell_price;//차익만큼 감소

        transaction_info memory new_transaction;
        new_transaction.buyer=my_add;
        new_transaction.seller=sell_add;
        new_transaction.price=sell_price;
        new_transaction.tranjection_n=my_index[my_add];
        new_transaction.my_coin=balances[my_add];
        
        transaction_list.push(new_transaction);//모델 정보 리스트로 구성해서 추가

        require(transaction_list.length!=0);//리스트가 없으면 더이상 진행안함

        emit TransactionCreate(msg.sender,_index);
    }
   
    //event ViewList()


   /* constructor(string memory name_, uint memory info_) public{
        _name=name_;
        _info=info_;
    }
    
    function name() public view returns (string memory) {//이름 정보 반환
        return _name;
    }

    function info() public view returns (uint memory){//가격 정보 반환
        return _info;
    }

     function send(address recipient, uint256 amount) public virtual override returns (bool) {
         require(msg.sender==minter);//함수를 실행한 사람이 minter컨트랙트 소유자일때만 진행
        _send(_msgSender(), recipient, amount);
        return true;
    }

    function mint(address receiver, uint amount) public{
        require(msg.sender==minter);//함수를 실행한 사람이 minter컨트랙트 소유자일때만 진행
        require(amount<1e60);
        balances[receiver]+=amount;
    }

    function _send(address sender, address recipient, uint256 amount) internal virtual {
        require(sender != address(0), "ERC20: transfer from the zero address");
        require(recipient != address(0), "ERC20: transfer to the zero address");

        _beforeTokenTransfer(sender, recipient, amount);

        balances[sender] = balances[sender].sub(amount, "ERC20: transfer amount exceeds balance");
        balances[recipient] = balances[recipient].add(amount);
        emit Transfer(sender, recipient, amount);
    }


    //ERC20 기준으로 실행전에 체크할 사항들을 여기에 넣어서 체크해 두거나 제한을 두고 싶은 사항들을 설정하라는 것
    function _beforeTokenTransfer(address from, address to, uint256 amount) internal virtual { }
*/
}