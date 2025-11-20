package com.romix.feature.feat446

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat446Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat446UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat446FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat446UserSummary
)

data class Feat446UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat446NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat446Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat446Config = Feat446Config()
) {

    fun loadSnapshot(userId: Long): Feat446NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat446NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat446UserSummary {
        return Feat446UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat446FeedItem> {
        val result = java.util.ArrayList<Feat446FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat446FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat446UiMapper {

    fun mapToUi(model: List<Feat446FeedItem>): Feat446UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat446UiModel(
            header = UiText("Feat446 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat446UiModel =
        Feat446UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat446UiModel =
        Feat446UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat446UiModel =
        Feat446UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat446Service(
    private val repository: Feat446Repository,
    private val uiMapper: Feat446UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat446UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat446UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat446UserItem1(val user: CoreUser, val label: String)
data class Feat446UserItem2(val user: CoreUser, val label: String)
data class Feat446UserItem3(val user: CoreUser, val label: String)
data class Feat446UserItem4(val user: CoreUser, val label: String)
data class Feat446UserItem5(val user: CoreUser, val label: String)
data class Feat446UserItem6(val user: CoreUser, val label: String)
data class Feat446UserItem7(val user: CoreUser, val label: String)
data class Feat446UserItem8(val user: CoreUser, val label: String)
data class Feat446UserItem9(val user: CoreUser, val label: String)
data class Feat446UserItem10(val user: CoreUser, val label: String)

data class Feat446StateBlock1(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock2(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock3(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock4(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock5(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock6(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock7(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock8(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock9(val state: Feat446UiModel, val checksum: Int)
data class Feat446StateBlock10(val state: Feat446UiModel, val checksum: Int)

fun buildFeat446UserItem(user: CoreUser, index: Int): Feat446UserItem1 {
    return Feat446UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat446StateBlock(model: Feat446UiModel): Feat446StateBlock1 {
    return Feat446StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat446UserSummary> {
    val list = java.util.ArrayList<Feat446UserSummary>(users.size)
    for (user in users) {
        list += Feat446UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat446UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat446UiModel {
    val summaries = (0 until count).map {
        Feat446UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat446UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat446UiModel> {
    val models = java.util.ArrayList<Feat446UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat446AnalyticsEvent1(val name: String, val value: String)
data class Feat446AnalyticsEvent2(val name: String, val value: String)
data class Feat446AnalyticsEvent3(val name: String, val value: String)
data class Feat446AnalyticsEvent4(val name: String, val value: String)
data class Feat446AnalyticsEvent5(val name: String, val value: String)
data class Feat446AnalyticsEvent6(val name: String, val value: String)
data class Feat446AnalyticsEvent7(val name: String, val value: String)
data class Feat446AnalyticsEvent8(val name: String, val value: String)
data class Feat446AnalyticsEvent9(val name: String, val value: String)
data class Feat446AnalyticsEvent10(val name: String, val value: String)

fun logFeat446Event1(event: Feat446AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat446Event2(event: Feat446AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat446Event3(event: Feat446AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat446Event4(event: Feat446AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat446Event5(event: Feat446AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat446Event6(event: Feat446AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat446Event7(event: Feat446AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat446Event8(event: Feat446AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat446Event9(event: Feat446AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat446Event10(event: Feat446AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat446Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat446Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat446(u: CoreUser): Feat446Projection1 =
    Feat446Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat446Projection1> {
    val list = java.util.ArrayList<Feat446Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat446(u)
    }
    return list
}
