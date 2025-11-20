package com.romix.feature.feat37

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat37Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat37UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat37FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat37UserSummary
)

data class Feat37UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat37NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat37Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat37Config = Feat37Config()
) {

    fun loadSnapshot(userId: Long): Feat37NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat37NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat37UserSummary {
        return Feat37UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat37FeedItem> {
        val result = java.util.ArrayList<Feat37FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat37FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat37UiMapper {

    fun mapToUi(model: List<Feat37FeedItem>): Feat37UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat37UiModel(
            header = UiText("Feat37 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat37UiModel =
        Feat37UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat37UiModel =
        Feat37UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat37UiModel =
        Feat37UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat37Service(
    private val repository: Feat37Repository,
    private val uiMapper: Feat37UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat37UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat37UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat37UserItem1(val user: CoreUser, val label: String)
data class Feat37UserItem2(val user: CoreUser, val label: String)
data class Feat37UserItem3(val user: CoreUser, val label: String)
data class Feat37UserItem4(val user: CoreUser, val label: String)
data class Feat37UserItem5(val user: CoreUser, val label: String)
data class Feat37UserItem6(val user: CoreUser, val label: String)
data class Feat37UserItem7(val user: CoreUser, val label: String)
data class Feat37UserItem8(val user: CoreUser, val label: String)
data class Feat37UserItem9(val user: CoreUser, val label: String)
data class Feat37UserItem10(val user: CoreUser, val label: String)

data class Feat37StateBlock1(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock2(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock3(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock4(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock5(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock6(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock7(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock8(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock9(val state: Feat37UiModel, val checksum: Int)
data class Feat37StateBlock10(val state: Feat37UiModel, val checksum: Int)

fun buildFeat37UserItem(user: CoreUser, index: Int): Feat37UserItem1 {
    return Feat37UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat37StateBlock(model: Feat37UiModel): Feat37StateBlock1 {
    return Feat37StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat37UserSummary> {
    val list = java.util.ArrayList<Feat37UserSummary>(users.size)
    for (user in users) {
        list += Feat37UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat37UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat37UiModel {
    val summaries = (0 until count).map {
        Feat37UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat37UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat37UiModel> {
    val models = java.util.ArrayList<Feat37UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat37AnalyticsEvent1(val name: String, val value: String)
data class Feat37AnalyticsEvent2(val name: String, val value: String)
data class Feat37AnalyticsEvent3(val name: String, val value: String)
data class Feat37AnalyticsEvent4(val name: String, val value: String)
data class Feat37AnalyticsEvent5(val name: String, val value: String)
data class Feat37AnalyticsEvent6(val name: String, val value: String)
data class Feat37AnalyticsEvent7(val name: String, val value: String)
data class Feat37AnalyticsEvent8(val name: String, val value: String)
data class Feat37AnalyticsEvent9(val name: String, val value: String)
data class Feat37AnalyticsEvent10(val name: String, val value: String)

fun logFeat37Event1(event: Feat37AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat37Event2(event: Feat37AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat37Event3(event: Feat37AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat37Event4(event: Feat37AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat37Event5(event: Feat37AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat37Event6(event: Feat37AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat37Event7(event: Feat37AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat37Event8(event: Feat37AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat37Event9(event: Feat37AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat37Event10(event: Feat37AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat37Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat37Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat37(u: CoreUser): Feat37Projection1 =
    Feat37Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat37Projection1> {
    val list = java.util.ArrayList<Feat37Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat37(u)
    }
    return list
}
