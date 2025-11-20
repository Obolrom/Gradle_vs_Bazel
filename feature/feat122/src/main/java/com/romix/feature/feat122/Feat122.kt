package com.romix.feature.feat122

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat122Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat122UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat122FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat122UserSummary
)

data class Feat122UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat122NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat122Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat122Config = Feat122Config()
) {

    fun loadSnapshot(userId: Long): Feat122NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat122NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat122UserSummary {
        return Feat122UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat122FeedItem> {
        val result = java.util.ArrayList<Feat122FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat122FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat122UiMapper {

    fun mapToUi(model: List<Feat122FeedItem>): Feat122UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat122UiModel(
            header = UiText("Feat122 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat122UiModel =
        Feat122UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat122UiModel =
        Feat122UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat122UiModel =
        Feat122UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat122Service(
    private val repository: Feat122Repository,
    private val uiMapper: Feat122UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat122UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat122UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat122UserItem1(val user: CoreUser, val label: String)
data class Feat122UserItem2(val user: CoreUser, val label: String)
data class Feat122UserItem3(val user: CoreUser, val label: String)
data class Feat122UserItem4(val user: CoreUser, val label: String)
data class Feat122UserItem5(val user: CoreUser, val label: String)
data class Feat122UserItem6(val user: CoreUser, val label: String)
data class Feat122UserItem7(val user: CoreUser, val label: String)
data class Feat122UserItem8(val user: CoreUser, val label: String)
data class Feat122UserItem9(val user: CoreUser, val label: String)
data class Feat122UserItem10(val user: CoreUser, val label: String)

data class Feat122StateBlock1(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock2(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock3(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock4(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock5(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock6(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock7(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock8(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock9(val state: Feat122UiModel, val checksum: Int)
data class Feat122StateBlock10(val state: Feat122UiModel, val checksum: Int)

fun buildFeat122UserItem(user: CoreUser, index: Int): Feat122UserItem1 {
    return Feat122UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat122StateBlock(model: Feat122UiModel): Feat122StateBlock1 {
    return Feat122StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat122UserSummary> {
    val list = java.util.ArrayList<Feat122UserSummary>(users.size)
    for (user in users) {
        list += Feat122UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat122UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat122UiModel {
    val summaries = (0 until count).map {
        Feat122UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat122UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat122UiModel> {
    val models = java.util.ArrayList<Feat122UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat122AnalyticsEvent1(val name: String, val value: String)
data class Feat122AnalyticsEvent2(val name: String, val value: String)
data class Feat122AnalyticsEvent3(val name: String, val value: String)
data class Feat122AnalyticsEvent4(val name: String, val value: String)
data class Feat122AnalyticsEvent5(val name: String, val value: String)
data class Feat122AnalyticsEvent6(val name: String, val value: String)
data class Feat122AnalyticsEvent7(val name: String, val value: String)
data class Feat122AnalyticsEvent8(val name: String, val value: String)
data class Feat122AnalyticsEvent9(val name: String, val value: String)
data class Feat122AnalyticsEvent10(val name: String, val value: String)

fun logFeat122Event1(event: Feat122AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat122Event2(event: Feat122AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat122Event3(event: Feat122AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat122Event4(event: Feat122AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat122Event5(event: Feat122AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat122Event6(event: Feat122AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat122Event7(event: Feat122AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat122Event8(event: Feat122AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat122Event9(event: Feat122AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat122Event10(event: Feat122AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat122Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat122Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat122(u: CoreUser): Feat122Projection1 =
    Feat122Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat122Projection1> {
    val list = java.util.ArrayList<Feat122Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat122(u)
    }
    return list
}
