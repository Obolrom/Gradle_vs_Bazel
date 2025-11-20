package com.romix.feature.feat597

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat597Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat597UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat597FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat597UserSummary
)

data class Feat597UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat597NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat597Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat597Config = Feat597Config()
) {

    fun loadSnapshot(userId: Long): Feat597NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat597NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat597UserSummary {
        return Feat597UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat597FeedItem> {
        val result = java.util.ArrayList<Feat597FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat597FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat597UiMapper {

    fun mapToUi(model: List<Feat597FeedItem>): Feat597UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat597UiModel(
            header = UiText("Feat597 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat597UiModel =
        Feat597UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat597UiModel =
        Feat597UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat597UiModel =
        Feat597UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat597Service(
    private val repository: Feat597Repository,
    private val uiMapper: Feat597UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat597UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat597UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat597UserItem1(val user: CoreUser, val label: String)
data class Feat597UserItem2(val user: CoreUser, val label: String)
data class Feat597UserItem3(val user: CoreUser, val label: String)
data class Feat597UserItem4(val user: CoreUser, val label: String)
data class Feat597UserItem5(val user: CoreUser, val label: String)
data class Feat597UserItem6(val user: CoreUser, val label: String)
data class Feat597UserItem7(val user: CoreUser, val label: String)
data class Feat597UserItem8(val user: CoreUser, val label: String)
data class Feat597UserItem9(val user: CoreUser, val label: String)
data class Feat597UserItem10(val user: CoreUser, val label: String)

data class Feat597StateBlock1(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock2(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock3(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock4(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock5(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock6(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock7(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock8(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock9(val state: Feat597UiModel, val checksum: Int)
data class Feat597StateBlock10(val state: Feat597UiModel, val checksum: Int)

fun buildFeat597UserItem(user: CoreUser, index: Int): Feat597UserItem1 {
    return Feat597UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat597StateBlock(model: Feat597UiModel): Feat597StateBlock1 {
    return Feat597StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat597UserSummary> {
    val list = java.util.ArrayList<Feat597UserSummary>(users.size)
    for (user in users) {
        list += Feat597UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat597UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat597UiModel {
    val summaries = (0 until count).map {
        Feat597UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat597UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat597UiModel> {
    val models = java.util.ArrayList<Feat597UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat597AnalyticsEvent1(val name: String, val value: String)
data class Feat597AnalyticsEvent2(val name: String, val value: String)
data class Feat597AnalyticsEvent3(val name: String, val value: String)
data class Feat597AnalyticsEvent4(val name: String, val value: String)
data class Feat597AnalyticsEvent5(val name: String, val value: String)
data class Feat597AnalyticsEvent6(val name: String, val value: String)
data class Feat597AnalyticsEvent7(val name: String, val value: String)
data class Feat597AnalyticsEvent8(val name: String, val value: String)
data class Feat597AnalyticsEvent9(val name: String, val value: String)
data class Feat597AnalyticsEvent10(val name: String, val value: String)

fun logFeat597Event1(event: Feat597AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat597Event2(event: Feat597AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat597Event3(event: Feat597AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat597Event4(event: Feat597AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat597Event5(event: Feat597AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat597Event6(event: Feat597AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat597Event7(event: Feat597AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat597Event8(event: Feat597AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat597Event9(event: Feat597AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat597Event10(event: Feat597AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat597Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat597Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat597(u: CoreUser): Feat597Projection1 =
    Feat597Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat597Projection1> {
    val list = java.util.ArrayList<Feat597Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat597(u)
    }
    return list
}
