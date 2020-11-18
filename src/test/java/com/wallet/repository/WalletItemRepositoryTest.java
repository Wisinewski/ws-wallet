package com.wallet.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.wallet.entity.Wallet;
import com.wallet.entity.WalletItem;
import com.wallet.util.enums.TypeEnum;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WalletItemRepositoryTest {

	private static final Date DATE = new Date();
	private static final TypeEnum TYPE = TypeEnum.EN;
	private static final String DESCRIPTION = "Conta de Luz";
	private static final BigDecimal VALUE = BigDecimal.valueOf(65);

	private Long savedWalletItemId;
	private Long savedWalletId;

	@Autowired
	WalletItemRepository walletItemRepository;

	@Autowired
	WalletRepository walletRepository;

	@Before
	public void setUp() {
		Wallet wallet = new Wallet();
		wallet.setName("carteira Teste");
		wallet.setValue(BigDecimal.valueOf(250));
		walletRepository.save(wallet);

		WalletItem walletItem = new WalletItem(null, wallet, DATE, TYPE, DESCRIPTION, VALUE);
		walletItemRepository.save(walletItem);

		savedWalletItemId = walletItem.getId();
		savedWalletId = wallet.getId();
	}

	@After
	public void tearDown() {
		walletItemRepository.deleteAll();
		walletRepository.deleteAll();
	}

	@Test
	public void testSave() {
		Wallet wallet = new Wallet();
		wallet.setName("Carteira 1");
		wallet.setValue(BigDecimal.valueOf(500));
		walletRepository.save(wallet);

		WalletItem walletItem = new WalletItem(1L, wallet, DATE, TYPE, DESCRIPTION, VALUE);

		WalletItem response = walletItemRepository.save(walletItem);

		assertNotNull(response);
		assertEquals(response.getDescription(), DESCRIPTION);
		assertEquals(response.getType(), TYPE);
		assertEquals(response.getValue(), VALUE);
		assertEquals(response.getWallet().getId(), wallet.getId());
	}

	@Test(expected = ConstraintViolationException.class)
	public void testSaveInvalidWalletItem() {
		WalletItem walletItem = new WalletItem(null, null, DATE, null, DESCRIPTION, null);
		walletItemRepository.save(walletItem);
	}

	@Test
	public void testUpdate() {
		Optional<WalletItem> walletItem = walletItemRepository.findById(savedWalletItemId);

		String description = "Descrição alterada";

		WalletItem changed = walletItem.get();
		changed.setDescription(description);

		walletItemRepository.save(changed);

		Optional<WalletItem> newWalletItem = walletItemRepository.findById(savedWalletItemId);

		assertEquals(description, newWalletItem.get().getDescription());
	}

	@Test
	public void deleteWalletItem() {
		Optional<Wallet> wallet = walletRepository.findById(savedWalletId);
		WalletItem wi = new WalletItem(null, wallet.get(), DATE, TYPE, DESCRIPTION, VALUE);

		walletItemRepository.save(wi);

		walletItemRepository.deleteById(wi.getId());

		Optional<WalletItem> response = walletItemRepository.findById(wi.getId());

		assertFalse(response.isPresent());
	}

	@Test
	public void testFindBetweenDates() {
		Optional<Wallet> w = walletRepository.findById(savedWalletId);

		LocalDateTime localDateTime = DATE.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		Date currentDatePlusFiveDays = Date.from(localDateTime.plusDays(5).atZone(ZoneId.systemDefault()).toInstant());
		Date currentDatePlusSevenDays = Date.from(localDateTime.plusDays(7).atZone(ZoneId.systemDefault()).toInstant());

		walletItemRepository.save(new WalletItem(null, w.get(), currentDatePlusFiveDays, TYPE, DESCRIPTION, VALUE));
		walletItemRepository.save(new WalletItem(null, w.get(), currentDatePlusSevenDays, TYPE, DESCRIPTION, VALUE));

		PageRequest pg = new PageRequest(0, 10);
		Page<WalletItem> response = walletItemRepository.findAllByWalletIdAndDateGreaterThanEqualAndDateLessThanEqual(
				savedWalletId, DATE, currentDatePlusFiveDays, pg);

		assertEquals(response.getContent().size(), 2);
		assertEquals(response.getTotalElements(), 2);
		assertEquals(response.getContent().get(0).getWallet().getId(), savedWalletId);
	}

	@Test
	public void testFindByType() {
		List<WalletItem> response = walletItemRepository.findByWalletIdAndType(savedWalletId, TYPE);

		assertEquals(response.size(), 1);
		assertEquals(response.get(0).getType(), TYPE);
	}

	@Test
	public void testFindByTypeSd() {

		Optional<Wallet> w = walletRepository.findById(savedWalletId);

		walletItemRepository.save(new WalletItem(null, w.get(), DATE, TypeEnum.SD, DESCRIPTION, VALUE));

		List<WalletItem> response = walletItemRepository.findByWalletIdAndType(savedWalletId, TypeEnum.SD);

		assertEquals(response.size(), 1);
		assertEquals(response.get(0).getType(), TypeEnum.SD);
	}

	@Test
	public void testSumByWallet() {
		Optional<Wallet> w = walletRepository.findById(savedWalletId);

		walletItemRepository.save(new WalletItem(null, w.get(), DATE, TYPE, DESCRIPTION, BigDecimal.valueOf(150.80)));

		BigDecimal response = walletItemRepository.sumByWalletId(savedWalletId);

		assertEquals(response.compareTo(BigDecimal.valueOf(215.8)), 0);
	}

}
