package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(itemValidator);// 컨트롤러가 호출될 때마다 @ModelAttribute마냥 계속해 호출돼서 내부적으로 요청이 올때마다 WebDataBinder이 만들어지고 itemValidator을 거기다가 넣어놓음 !
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {

        //그럼 굳이 오류 결과를 담을 공간을 생성 안 해도 되네 ??
        Map<String, String> errors = new HashMap<>();
        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item" , "itemName" , "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item" , "price" , "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item" , "quantity" ,  "수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        // 특정 필드가 아닌 전역 오류이니 그냥 ObjectError로 처리하고 특정 필드 지정 안 해도 됨 전역 에러이니 !
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item" , "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }
        //에러가 있으면 ! 하는 직관적인 로직임 NOT 연산자 필요 없음
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //BindingResult는 모델에 자동으로 담기 때문에
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item" , "itemName" , item.getItemName(), false, null , null , "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item" , "price" , item.getPrice(), false, null , null , "가격은 1,000 ~ 1,000,000 까지 허용합니다."));

        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item" , "quantity" , item.getQuantity(), false, null , null , "수량은 최대 9,999 까지 허용합니다."));

        }

        //특정 필드가 아닌 복합 룰 검증
        // 특정 필드가 아닌 전역 오류이니 그냥 ObjectError로 처리하고 특정 필드 지정 안 해도 됨 전역 에러이니 !
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item" , null ,null ,"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }
        //에러가 있으면 ! 하는 직관적인 로직임 NOT 연산자 필요 없음
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //BindingResult는 모델에 자동으로 담기 때문에
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item" , "itemName" , item.getItemName(), false, new String[]{"required.item.itemName"} , null , null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item" , "price" , item.getPrice(), false, new String[]{"range.item.price"} , new Object[]{1000 , 1000000} , null));

        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item" , "quantity" , item.getQuantity(), false, new String[]{"max.item.quantity"} , new Object[]{9999} , null));

        }

        //특정 필드가 아닌 복합 룰 검증
        // 특정 필드가 아닌 전역 오류이니 그냥 ObjectError로 처리하고 특정 필드 지정 안 해도 됨 전역 에러이니 !
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item" , new String[]{"totalPriceMin"} ,new Object[]{10000 , resultPrice} , null ));
            }
        }
        //에러가 있으면 ! 하는 직관적인 로직임 NOT 연산자 필요 없음
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //BindingResult는 모델에 자동으로 담기 때문에
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    //@PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {
        log.info("ObjectName = {}" , bindingResult.getObjectName());
        log.info("Target = {}" , bindingResult.getTarget());

        //검증 로직
        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName" , "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price" , "range" , new Object[]{1000 , 10000} , null);

        }

        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.rejectValue("quantity" , "max" , new Object[]{9999} , null);

        }

        //특정 필드가 아닌 복합 룰 검증
        // 특정 필드가 아닌 전역 오류이니 그냥 ObjectError로 처리하고 특정 필드 지정 안 해도 됨 전역 에러이니 !
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject( "totalPriceMin" , new Object[]{10000 , resultPrice} , null);
            }
        }
        //에러가 있으면 ! 하는 직관적인 로직임 NOT 연산자 필요 없음
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //BindingResult는 모델에 자동으로 담기 때문에
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {

        if (itemValidator.supports(Item.class)){ //supports로 Item  클래스를 지원하는지 안하는지 검증 후 지원하면 validate 호출 !
            itemValidator.validate(item , bindingResult); // validate는 검증할 객체와 오류 객체를 저장하기 위한 bindingResult를 던져서 로직 수행
        }

        //에러가 있으면 ! 하는 직관적인 로직임 NOT 연산자 필요 없음
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //BindingResult는 모델에 자동으로 담기 때문에
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult , RedirectAttributes redirectAttributes, Model model) {



        //에러가 있으면 ! 하는 직관적인 로직임 NOT 연산자 필요 없음
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            //BindingResult는 모델에 자동으로 담기 때문에
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

