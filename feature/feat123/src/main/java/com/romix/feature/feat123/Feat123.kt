package com.romix.feature.feat123

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat123Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat123UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat123FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat123UserSummary
)

data class Feat123UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat123NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat123Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat123Config = Feat123Config()
) {

    fun loadSnapshot(userId: Long): Feat123NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat123NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat123UserSummary {
        return Feat123UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat123FeedItem> {
        val result = java.util.ArrayList<Feat123FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat123FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat123UiMapper {

    fun mapToUi(model: List<Feat123FeedItem>): Feat123UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat123UiModel(
            header = UiText("Feat123 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat123UiModel =
        Feat123UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat123UiModel =
        Feat123UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat123UiModel =
        Feat123UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat123Service(
    private val repository: Feat123Repository,
    private val uiMapper: Feat123UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat123UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat123UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat123UserItem1(val user: CoreUser, val label: String)
data class Feat123UserItem2(val user: CoreUser, val label: String)
data class Feat123UserItem3(val user: CoreUser, val label: String)
data class Feat123UserItem4(val user: CoreUser, val label: String)
data class Feat123UserItem5(val user: CoreUser, val label: String)
data class Feat123UserItem6(val user: CoreUser, val label: String)
data class Feat123UserItem7(val user: CoreUser, val label: String)
data class Feat123UserItem8(val user: CoreUser, val label: String)
data class Feat123UserItem9(val user: CoreUser, val label: String)
data class Feat123UserItem10(val user: CoreUser, val label: String)

data class Feat123StateBlock1(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock2(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock3(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock4(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock5(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock6(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock7(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock8(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock9(val state: Feat123UiModel, val checksum: Int)
data class Feat123StateBlock10(val state: Feat123UiModel, val checksum: Int)

fun buildFeat123UserItem(user: CoreUser, index: Int): Feat123UserItem1 {
    return Feat123UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat123StateBlock(model: Feat123UiModel): Feat123StateBlock1 {
    return Feat123StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat123UserSummary> {
    val list = java.util.ArrayList<Feat123UserSummary>(users.size)
    for (user in users) {
        list += Feat123UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat123UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat123UiModel {
    val summaries = (0 until count).map {
        Feat123UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat123UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat123UiModel> {
    val models = java.util.ArrayList<Feat123UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat123AnalyticsEvent1(val name: String, val value: String)
data class Feat123AnalyticsEvent2(val name: String, val value: String)
data class Feat123AnalyticsEvent3(val name: String, val value: String)
data class Feat123AnalyticsEvent4(val name: String, val value: String)
data class Feat123AnalyticsEvent5(val name: String, val value: String)
data class Feat123AnalyticsEvent6(val name: String, val value: String)
data class Feat123AnalyticsEvent7(val name: String, val value: String)
data class Feat123AnalyticsEvent8(val name: String, val value: String)
data class Feat123AnalyticsEvent9(val name: String, val value: String)
data class Feat123AnalyticsEvent10(val name: String, val value: String)

fun logFeat123Event1(event: Feat123AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat123Event2(event: Feat123AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat123Event3(event: Feat123AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat123Event4(event: Feat123AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat123Event5(event: Feat123AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat123Event6(event: Feat123AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat123Event7(event: Feat123AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat123Event8(event: Feat123AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat123Event9(event: Feat123AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat123Event10(event: Feat123AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat123Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat123Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat123(u: CoreUser): Feat123Projection1 =
    Feat123Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat123Projection1> {
    val list = java.util.ArrayList<Feat123Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat123(u)
    }
    return list
}
