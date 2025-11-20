package com.romix.feature.feat503

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat503Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat503UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat503FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat503UserSummary
)

data class Feat503UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat503NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat503Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat503Config = Feat503Config()
) {

    fun loadSnapshot(userId: Long): Feat503NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat503NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat503UserSummary {
        return Feat503UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat503FeedItem> {
        val result = java.util.ArrayList<Feat503FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat503FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat503UiMapper {

    fun mapToUi(model: List<Feat503FeedItem>): Feat503UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat503UiModel(
            header = UiText("Feat503 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat503UiModel =
        Feat503UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat503UiModel =
        Feat503UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat503UiModel =
        Feat503UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat503Service(
    private val repository: Feat503Repository,
    private val uiMapper: Feat503UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat503UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat503UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat503UserItem1(val user: CoreUser, val label: String)
data class Feat503UserItem2(val user: CoreUser, val label: String)
data class Feat503UserItem3(val user: CoreUser, val label: String)
data class Feat503UserItem4(val user: CoreUser, val label: String)
data class Feat503UserItem5(val user: CoreUser, val label: String)
data class Feat503UserItem6(val user: CoreUser, val label: String)
data class Feat503UserItem7(val user: CoreUser, val label: String)
data class Feat503UserItem8(val user: CoreUser, val label: String)
data class Feat503UserItem9(val user: CoreUser, val label: String)
data class Feat503UserItem10(val user: CoreUser, val label: String)

data class Feat503StateBlock1(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock2(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock3(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock4(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock5(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock6(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock7(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock8(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock9(val state: Feat503UiModel, val checksum: Int)
data class Feat503StateBlock10(val state: Feat503UiModel, val checksum: Int)

fun buildFeat503UserItem(user: CoreUser, index: Int): Feat503UserItem1 {
    return Feat503UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat503StateBlock(model: Feat503UiModel): Feat503StateBlock1 {
    return Feat503StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat503UserSummary> {
    val list = java.util.ArrayList<Feat503UserSummary>(users.size)
    for (user in users) {
        list += Feat503UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat503UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat503UiModel {
    val summaries = (0 until count).map {
        Feat503UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat503UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat503UiModel> {
    val models = java.util.ArrayList<Feat503UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat503AnalyticsEvent1(val name: String, val value: String)
data class Feat503AnalyticsEvent2(val name: String, val value: String)
data class Feat503AnalyticsEvent3(val name: String, val value: String)
data class Feat503AnalyticsEvent4(val name: String, val value: String)
data class Feat503AnalyticsEvent5(val name: String, val value: String)
data class Feat503AnalyticsEvent6(val name: String, val value: String)
data class Feat503AnalyticsEvent7(val name: String, val value: String)
data class Feat503AnalyticsEvent8(val name: String, val value: String)
data class Feat503AnalyticsEvent9(val name: String, val value: String)
data class Feat503AnalyticsEvent10(val name: String, val value: String)

fun logFeat503Event1(event: Feat503AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat503Event2(event: Feat503AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat503Event3(event: Feat503AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat503Event4(event: Feat503AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat503Event5(event: Feat503AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat503Event6(event: Feat503AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat503Event7(event: Feat503AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat503Event8(event: Feat503AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat503Event9(event: Feat503AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat503Event10(event: Feat503AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat503Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat503Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat503(u: CoreUser): Feat503Projection1 =
    Feat503Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat503Projection1> {
    val list = java.util.ArrayList<Feat503Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat503(u)
    }
    return list
}
