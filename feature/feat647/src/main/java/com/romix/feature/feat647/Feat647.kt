package com.romix.feature.feat647

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat647Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat647UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat647FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat647UserSummary
)

data class Feat647UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat647NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat647Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat647Config = Feat647Config()
) {

    fun loadSnapshot(userId: Long): Feat647NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat647NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat647UserSummary {
        return Feat647UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat647FeedItem> {
        val result = java.util.ArrayList<Feat647FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat647FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat647UiMapper {

    fun mapToUi(model: List<Feat647FeedItem>): Feat647UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat647UiModel(
            header = UiText("Feat647 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat647UiModel =
        Feat647UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat647UiModel =
        Feat647UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat647UiModel =
        Feat647UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat647Service(
    private val repository: Feat647Repository,
    private val uiMapper: Feat647UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat647UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat647UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat647UserItem1(val user: CoreUser, val label: String)
data class Feat647UserItem2(val user: CoreUser, val label: String)
data class Feat647UserItem3(val user: CoreUser, val label: String)
data class Feat647UserItem4(val user: CoreUser, val label: String)
data class Feat647UserItem5(val user: CoreUser, val label: String)
data class Feat647UserItem6(val user: CoreUser, val label: String)
data class Feat647UserItem7(val user: CoreUser, val label: String)
data class Feat647UserItem8(val user: CoreUser, val label: String)
data class Feat647UserItem9(val user: CoreUser, val label: String)
data class Feat647UserItem10(val user: CoreUser, val label: String)

data class Feat647StateBlock1(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock2(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock3(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock4(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock5(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock6(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock7(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock8(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock9(val state: Feat647UiModel, val checksum: Int)
data class Feat647StateBlock10(val state: Feat647UiModel, val checksum: Int)

fun buildFeat647UserItem(user: CoreUser, index: Int): Feat647UserItem1 {
    return Feat647UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat647StateBlock(model: Feat647UiModel): Feat647StateBlock1 {
    return Feat647StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat647UserSummary> {
    val list = java.util.ArrayList<Feat647UserSummary>(users.size)
    for (user in users) {
        list += Feat647UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat647UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat647UiModel {
    val summaries = (0 until count).map {
        Feat647UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat647UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat647UiModel> {
    val models = java.util.ArrayList<Feat647UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat647AnalyticsEvent1(val name: String, val value: String)
data class Feat647AnalyticsEvent2(val name: String, val value: String)
data class Feat647AnalyticsEvent3(val name: String, val value: String)
data class Feat647AnalyticsEvent4(val name: String, val value: String)
data class Feat647AnalyticsEvent5(val name: String, val value: String)
data class Feat647AnalyticsEvent6(val name: String, val value: String)
data class Feat647AnalyticsEvent7(val name: String, val value: String)
data class Feat647AnalyticsEvent8(val name: String, val value: String)
data class Feat647AnalyticsEvent9(val name: String, val value: String)
data class Feat647AnalyticsEvent10(val name: String, val value: String)

fun logFeat647Event1(event: Feat647AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat647Event2(event: Feat647AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat647Event3(event: Feat647AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat647Event4(event: Feat647AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat647Event5(event: Feat647AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat647Event6(event: Feat647AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat647Event7(event: Feat647AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat647Event8(event: Feat647AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat647Event9(event: Feat647AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat647Event10(event: Feat647AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat647Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat647Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat647(u: CoreUser): Feat647Projection1 =
    Feat647Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat647Projection1> {
    val list = java.util.ArrayList<Feat647Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat647(u)
    }
    return list
}
